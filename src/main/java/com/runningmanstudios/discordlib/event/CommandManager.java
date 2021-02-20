package com.runningmanstudios.discordlib.event;

import com.runningmanstudios.discordlib.DiscordBot;
import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.command.Attractor;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.SQLDataBase;
import com.runningmanstudios.discordlib.data.MemberData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CommandManager extends ListenerAdapter {
    DecimalFormat df = new DecimalFormat("0.##");

    Map<String, Attractor> attractors = new HashMap<>();

    final String prefix;
    List<Command> commandList = new ArrayList<>();
    DiscordBot bot;

    public CommandManager(DiscordBot bot) {
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

        MemberData userData = SQLDataBase.getMemberData(event.getGuild().getId(), event.getAuthor().getId());
        if (userData == null) {
            SQLDataBase.addMemberData(event.getGuild().getId(), event.getAuthor().getId());
            userData = SQLDataBase.getMemberData(event.getGuild().getId(), event.getAuthor().getId());
        }

        Attractor attractor = attractors.get(event.getAuthor().getId());
        if (attractor!=null) {
            Instant then = attractor.getStart();
            Instant now = Instant.now();
            Duration timeElapsed = Duration.between(then, now);
            if (attractor.textEquals(event.getMessage().getContentRaw()) && !(timeElapsed.toMinutes() > bot.getSettingsInt("max_attract_wait_min"))) {
                attractor.getListener().onAttract(new BotMessageEvent(event, event.getMessage().getContentRaw().split(" "), this));
                attractor.updateTime();
                return;
            } else stopAttracting(event.getAuthor());
        }

        Random roll = new Random();

        //coins
        int coinAmt = roll.nextInt(10) + 1;
        int coinBase = roll.nextInt(20) + 1;

        if (coinAmt == coinBase) {
            int coins = userData.coins + coinAmt;
            userData = userData.withCoins(coins);
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(255, 0, 0))
                    .setAuthor(event.getAuthor().getName())
                    .addField("🪙", coinAmt + " coins added!", true)
                    .setFooter("do `" + prefix + " bank` to see your balance");
            event.getChannel().sendMessage(embed.build()).queue((result) -> result.delete().queueAfter(3, TimeUnit.SECONDS), Throwable::printStackTrace);
        }

        //xp
        int xpAdd = roll.nextInt(7) + 8;
        int xpToNextLvl = userData.level * 175;

        userData = userData.withXP(userData.xp + xpAdd);
        if (xpToNextLvl <= userData.xp) {
            userData = userData.withXP(xpToNextLvl - userData.xp);
            userData = userData.withLevel(userData.level + 1);
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(0, 255, 0))
                    .setTitle(event.getAuthor().getName() + " Leveled Up!")
                    .addField("\uD83D\uDD3C", userData.level + 1 + "", true)
                    .setFooter("do `" + prefix + " level` to see your xp");
            event.getChannel().sendMessage(embed.build()).queue((result) -> result.delete().queueAfter(5, TimeUnit.SECONDS), Throwable::printStackTrace);
        }

        //magic
/*        if (user.get("dungeon")!=null) {
            float magicAdd = (roll.nextInt(7) + 8)/100f;
            JSONObject dungeon = (JSONObject) user.get("dungeon");
            float curMagic = ((Number) dungeon.get("magic")).floatValue();

            dungeon.replace("magic", Float.valueOf(df.format(curMagic + magicAdd)));
        }*/

        SQLDataBase.updateMemberData(userData);

        String messageContent = event.getMessage().getContentRaw();
        if (messageContent.length() < prefix.length() || !messageContent.substring(0, prefix.length()).equalsIgnoreCase(prefix)) return;

        messageContent = messageContent.substring(prefix.length());

        String[] args = messageContent.split(" ");
        String commandName = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);

        for (Command command : commandList) {
            CommandBuilder builder = command.getClass().getAnnotation(CommandBuilder.class);
            boolean nameMatch = false;
            if (builder.name().equalsIgnoreCase(commandName)) nameMatch = true;
            for (String alias : builder.aliases()) {
                if (alias.equalsIgnoreCase(commandName)) {
                    nameMatch = true;
                    break;
                }
            }
            if (!nameMatch) continue;

            try {
                command.onMessage(new BotMessageEvent(event, args, this));
            } catch (Exception e) {
                event.getChannel().sendMessage("There was an error running the command. Remember that the correct usages for this command would be " + Util.codeArrayToString(getUsages(prefix, builder))).queue((result) -> result.delete().queueAfter(10, TimeUnit.SECONDS), Throwable::printStackTrace);
                bot.writeToTraceBack(e);
            }
        }
    }

    public List<Command> getCommands() {
        return commandList;
    }

    public DiscordBot getBot() {
        return bot;
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

    public void setAttractor(User user, Attractor attractor) {
        this.attractors.put(user.getId(), attractor);
    }

    public Attractor getAttractor(User user) {
        return this.attractors.getOrDefault(user.getId(), null);
    }

    public void stopAttracting(User user) {
        this.attractors.remove(user.getId());
    }

}
