package com.runningmanstudios.discordlib.event;

import com.runningmanstudios.discordlib.Bot;
import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.command.AttractInfo;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.DataBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.awt.*;
import java.io.File;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandManager extends ListenerAdapter {
    DecimalFormat df = new DecimalFormat("0.##");

    Map<String, AttractInfo> attractors = new HashMap<>();

    final String prefix;
    List<Command> commandList = new ArrayList<>();
    Bot bot;

    public CommandManager(Bot bot) {
        this.bot = bot;
        this.prefix = bot.getPrefix();
    }

    public void addCommand(Command command) {
        commandList.add(command);
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        try {
            JSONObject status = (JSONObject) bot.settings.get("status");
            if (status != null) {
                String type = (String) status.get("type");
                String content = (String) status.get("content");
                switch (type) {
                    case "playing" ->
                            event.getJDA().getPresence().setActivity(Activity.playing(content));
                    case "listening" ->
                            event.getJDA().getPresence().setActivity(Activity.listening(content));
                    case "streaming" -> {
                        String url = (String) status.get("url");
                        event.getJDA().getPresence().setActivity(Activity.streaming(content, url));
                    }
                    case "watching" ->
                            event.getJDA().getPresence().setActivity(Activity.watching(content));
                    case "null" ->
                            event.getJDA().getPresence().setActivity(null);
                    case "custom" ->
                            event.getJDA().getPresence().setActivity(Activity.of(Activity.ActivityType.CUSTOM_STATUS, content));
                    default ->
                            throw new RuntimeException("Unknown type: " + type);
                }
            } else {
                event.getJDA().getPresence().setActivity(null);
            }
        } catch (Exception e) {
            new Exception("There was an error setting up the status. Make sure you are using the correct formatting in BotInfo.json", e).printStackTrace();
            System.exit(1);
        }
        System.out.println("The Bot is Ready for action!");
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getMember().getUser().isBot()) return;

        if (!bot.users.containsSection(event.getAuthor().getId())) {
            bot.users.addSection(event.getAuthor().getId());
        }

        AttractInfo attractor = attractors.get(event.getAuthor().getId());
        if (attractor!=null) {
            Instant then = attractor.getStart();
            Instant now = Instant.now();
            Duration timeElapsed = Duration.between(then, now);
            if (attractor.textEquals(event.getMessage().getContentRaw()) && !(timeElapsed.toMinutes() > ((Number)bot.settings.get("max_attract_wait_min")).intValue())) {
                attractor.getCommand().onAttract(new CommandEvent(event, event.getMessage().getContentRaw().split(" "), this));
                attractor.updateTime();
                return;
            } else stopAttracting(event.getAuthor());
        }

        JSONObject user = bot.users.getSection(event.getAuthor().getId());
        if (!user.containsKey("coins")) {
            user.put("coins", 0);
        }
        if (!user.containsKey("xp")) {
            user.put("xp", 0);
        }
        if (!user.containsKey("level")) {
            user.put("level", 1);
        }
        Random roll = new Random();

        //coins
        int coinAmt = roll.nextInt(15) + 1;
        int coinBase = roll.nextInt(15) + 1;

        if (coinAmt == coinBase) {
            long coins = (long) user.get("coins");
            user.replace("coins", coins + coinAmt);
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(255, 0, 0))
                    .setAuthor(event.getAuthor().getName())
                    .addField("\uD83D\uDCB8", coinAmt + " coins added!", true)
                    .setFooter("do `" + prefix + " bank` to see your balance");
            event.getChannel().sendMessage(embed.build()).queue((result) -> result.delete().queueAfter(3, TimeUnit.SECONDS), Throwable::printStackTrace);
        }

        //xp
        long xpAdd = roll.nextInt(7) + 8;
        long curXp = ((Number) user.get("xp")).longValue();
        long curLvl = ((Number) user.get("level")).longValue();
        long xpToNextLvl = curLvl * 150;

        user.replace("xp", curXp + xpAdd);
        curXp = ((Number) user.get("xp")).longValue();
        if (xpToNextLvl <= curXp) {
            user.replace("level", curLvl + 1);
            user.replace("xp", xpToNextLvl - curXp);
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(0, 255, 0))
                    .setTitle(event.getAuthor().getName() + " Leveled Up!")
                    .addField("\uD83D\uDD3C", curLvl + 1 + "", true)
                    .setFooter("do `" + prefix + " level` to see your xp");
            event.getChannel().sendMessage(embed.build()).queue((result) -> result.delete().queueAfter(5, TimeUnit.SECONDS), Throwable::printStackTrace);
        }

        //magic
        if (user.get("dungeon")!=null) {
            float magicAdd = (roll.nextInt(7) + 8)/100f;
            JSONObject dungeon = (JSONObject) user.get("dungeon");
            float curMagic = ((Number) dungeon.get("magic")).floatValue();

            dungeon.replace("magic", Float.valueOf(df.format(curMagic + magicAdd)));
        }


        //write the users to
        bot.users.writeContent();

        String messageContent = event.getMessage().getContentRaw();
        if (!messageContent.startsWith(prefix)) return;

        messageContent = messageContent.substring(prefix.length());

        String[] args = messageContent.split(" ");
        String commandName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        for (Command command : commandList) {
            CommandBuilder builder = command.getClass().getAnnotation(CommandBuilder.class);
            if (!builder.name().equals(commandName) && !Arrays.asList(builder.aliases()).contains(commandName)) continue;

            try {
                command.onMessage(new CommandEvent(event, args, this));
            } catch (Exception e) {
                event.getChannel().sendMessage("There was an error running the command. Remember that the correct usages for this command would be " + Util.codeArrayToString(getUsages(prefix, builder))).queue((result) -> result.delete().queueAfter(10, TimeUnit.SECONDS), Throwable::printStackTrace);
                new File(System.getProperty("user.home") + bot.getDataLocation() + File.separator + "traceback.txt");
            }
        }
    }

    public List<Command> getCommands() {
        return commandList;
    }

    public String getPrefix() {
        return prefix;
    }

    public Bot getBot() {
        return bot;
    }

    public DataBase findJSON(String s) {
        return new DataBase(bot.getDataLocation()+s);
    }

    public static String[] getUsages(String prefix, CommandBuilder command) {
        if (command.usages().length == 0)
            return new String[] {prefix + command.name()};

        String[] cleanUsages = new String[command.usages().length];

        for (int i = 0; i < command.usages().length; i++) {
            String usage = command.usages()[i];
            if (usage.equals(""))
                cleanUsages[i] = prefix + command.name();
            else
                cleanUsages[i] = prefix + command.name() + " " + usage;
        }

        return cleanUsages;
    }

    public void setAttractor(User user, AttractInfo attractor) {
        this.attractors.put(user.getId(), attractor);
    }

    public AttractInfo getAttractor(User user) {
        return this.attractors.getOrDefault(user.getId(), null);
    }

    public void stopAttracting(User user) {
        this.attractors.remove(user.getId());
    }

}
