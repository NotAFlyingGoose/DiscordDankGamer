package com.runningmanstudios.discordlib;

import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.data.SQLDataBase;
import com.runningmanstudios.discordlib.data.Inventory;
import com.runningmanstudios.discordlib.data.Item;
import com.runningmanstudios.discordlib.data.MemberData;
import com.runningmanstudios.discordlib.event.CommandManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class DiscordBot {
    private static final String baseLocation = System.getProperty("user.home");
    private final File traceback;
    private final File dataLocation;
    public final CommandManager commandManager;
    public JSONObject items;
    public JSONObject data;
    public JSONObject settings;
    public JDA jda;
    private final String prefix;

    public DiscordBot(String dataLocation) {
        File location = new File(baseLocation + dataLocation);
        this.dataLocation = location;
        location.mkdirs();

        this.traceback = new File(location.getAbsolutePath() + File.separator + "traceback.txt");
        try {
            traceback.delete();
            traceback.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.settings = findJSON("Settings.json");

        SQLDataBase.setIP(getSettingsString("sql_ip"));
        SQLDataBase.setUsername(getSettingsString("sql_username"));
        SQLDataBase.setPassword(getSettingsString("sql_password"));

        String token = getSettingsString("token");
        this.prefix = getSettingsString("prefix");

        SQLDataBase.init();
        Util.setUserCoins("714930958561968168", "536248477324410881", 1000);

        items = findJSON("items.json");
        data = findJSON("data.json");
        try {
            jda = JDABuilder.createDefault(token).build();
        } catch (LoginException e) {
            System.err.println("There was an error when attempting to sign in:");
            e.printStackTrace();
            System.exit(1);
        }
        commandManager = new CommandManager(this);
        jda.addEventListener(commandManager);
    }

    public Object getSettingsObject(String token) {
        Object raw;
        try {
            raw = settings.get(token);
            if (raw == null) {
                throw new NullPointerException("Non-Existent value");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("There was an error while trying to get the string \"" + token + "\" from the json " + dataLocation.getAbsolutePath() + File.separator + "Settings.json", e);
        }
        return raw;
    }

    public String getSettingsString(String token) {
        return (String) getSettingsObject(token);
    }

    public int getSettingsInt(String token) {
        return ((Number) getSettingsObject(token)).intValue();
    }

    public float getSettingsFloat(String token) {
        return ((Number) getSettingsObject(token)).floatValue();
    }

    public JSONObject findJSON(String filename) {
        JSONObject result;
        try {
            //JSON parser object to parse read file
            JSONParser jsonParser = new JSONParser();

            FileReader reader = new FileReader(dataLocation.getAbsolutePath() + File.separator + filename);
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            result = (JSONObject) obj;

            reader.close();
            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("There was an error while trying to locate the json " + dataLocation.getAbsolutePath() + File.separator + filename, e);
        }
    }

    public void giveUserItem(MemberData member, String itemId, int amount) {
        Item item = getItem(itemId);
        if (item == null) throw new IllegalArgumentException("No item data for " + itemId);
        Inventory inv = new Inventory(this, member.inventory).give(item, amount);
        member = member.withInventory(inv.toDataString());

        SQLDataBase.updateMemberData(member);
        System.out.println(SQLDataBase.getMemberData(member.guildId, member.userId).inventory);
    }

    public void takeUserItem(MemberData member, String itemId, int amount) {
        Item item = getItem(itemId);
        if (item == null) throw new IllegalArgumentException("No item data for " + itemId);
        member = member.withInventory(new Inventory(this, member.inventory).take(item, amount).toDataString());
        String code = """
                        USE DankGamer;
                        UPDATE Users 
                        SET inventory = ?
                        WHERE userid = ? AND guildid = ?""";
        try (Connection connection = SQLDataBase.createConnection();
             PreparedStatement statement = connection.prepareStatement(code)) {
            statement.setString(1, member.inventory);

            statement.setString(2, member.userId);
            statement.setString(3, member.guildId);
            System.out.println(statement.executeUpdate());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public File getDataLocation() {
        return dataLocation;
    }

    public void addCommand(Command command) {
        commandManager.addCommand(command);
    }

    public String getItemsByRarity(Iterable<Object> itemIds, float multiplier) {
        List<String> allitems = new LinkedList<>();
        List<String> commonitems = new LinkedList<>();
        List<String> uncommonitems = new LinkedList<>();
        List<String> rareitems = new LinkedList<>();
        List<String> epicitems = new LinkedList<>();
        List<String> legendaryitems = new LinkedList<>();
        for (Object item : itemIds) {
            int r = getItem(item.toString()).getRarity();
            if (r == 1) {
                commonitems.add(item.toString());
            } else if (r == 2) {
                uncommonitems.add(item.toString());
            } else if (r == 3) {
                rareitems.add(item.toString());
            } else if (r == 4) {
                epicitems.add(item.toString());
            } else if (r == 5) {
                legendaryitems.add(item.toString());
            }
        }
        allitems.addAll(commonitems);
        allitems.addAll(uncommonitems);
        allitems.addAll(rareitems);
        allitems.addAll(epicitems);
        allitems.addAll(legendaryitems);
        double result = Math.random();
        result = Math.pow(result, multiplier);
        result *= allitems.size();
        result = Math.floor(result);
        return allitems.get((int) result);
    }

    public Item getItem(String itemId) {
        Object obj = this.items.get(itemId);
        if (obj == null) return null;
        JSONObject itemData = (JSONObject) obj;
        return new Item(itemId, (String) itemData.get("name"), (String) itemData.get("icon"), ((Number) itemData.get("rarity")).intValue());
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void writeToTraceBack(Throwable t) {
        try {
            FileWriter fw = new FileWriter(traceback, true);
            Set<Throwable> dejaVu = Collections.newSetFromMap(new IdentityHashMap<>());
            dejaVu.add(t);

            fw.write(t.toString() + "\n");
            StackTraceElement[] trace = t.getStackTrace();

            // Write the stack trace
            for (StackTraceElement traceElement : trace)
                fw.write("\tat " + traceElement + "\n");

            // Write suppressed exceptions, if any
            for (Throwable se : t.getSuppressed())
                writeEnclosedTraceBack(fw, se, trace, "Suppressed: ", prefix + "\t", dejaVu);

            // Write cause, if any
            Throwable ourCause = t.getCause();
            if (ourCause != null)
                writeEnclosedTraceBack(fw, ourCause, trace, "Caused by: ", prefix, dejaVu);

            fw.write("\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeEnclosedTraceBack(FileWriter traceBackWriter, Throwable t, StackTraceElement[] enclosingTrace, String caption, String prefix, Set<Throwable> dejaVu) throws IOException {
        if (dejaVu.contains(t)) {
            traceBackWriter.write(prefix + caption + "[CIRCULAR REFERENCE: " + this + "]\n");
        } else {
            dejaVu.add(t);
            // Compute number of frames in common between this and enclosing trace
            StackTraceElement[] trace = t.getStackTrace();
            int m = trace.length - 1;
            int n = enclosingTrace.length - 1;
            while (m >= 0 && n >= 0 && trace[m].equals(enclosingTrace[n])) {
                m--;
                n--;
            }
            int framesInCommon = trace.length - 1 - m;

            // Print our stack trace
            traceBackWriter.write(prefix + caption + this + "\n");
            for (int i = 0; i <= m; i++)
                traceBackWriter.write(prefix + "\tat " + trace[i] + "\n");
            if (framesInCommon != 0)
                traceBackWriter.write(prefix + "\t... " + framesInCommon + " more\n");

            // Print suppressed exceptions, if any
            for (Throwable se : t.getSuppressed())
                writeEnclosedTraceBack(traceBackWriter, se, trace, "Suppressed: ", prefix + "\t", dejaVu);

            // Print cause, if any
            Throwable ourCause = t.getCause();
            if (ourCause != null)
                writeEnclosedTraceBack(traceBackWriter, ourCause, trace, "Caused by: ", prefix, dejaVu);
        }
    }


}
