package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.CommandEvent;
import com.runningmanstudios.discordlib.event.CommandManager;
import net.dv8tion.jda.api.EmbedBuilder;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandBuilder(name = "help",
        description = "provides a list of commands, or if you specify you can see mre data about a single command",
        usages = {"", "page <number>", "<command name>"})
public class HelpCommand implements Command {

    @Override
    public void onMessage(CommandEvent event) {
        if (event.getArgs().length == 0) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Help Menu")
                    .setColor(new Color(255, 255, 0))
                    .setTimestamp(null);
            embed.addField("**To see all commands:**", "type `" + event.getCommandManager().getBot().getPrefix() + "help page <number>`. replacing `<number>` with the page that you want to see.", false);
            embed.addField("**To get more info on a specific command:**", "type `" + event.getCommandManager().getBot().getPrefix() + "help <command name>`. replacing `<command name>` with the command that you want to see.", false);
            event.getChannel().sendMessage(embed.build()).queue();
            return;
        }

        if (event.getArgs()[0].equalsIgnoreCase("page")) {
            int page = Integer.parseInt(event.getArgs()[1]);
            List<Command> commands = event.getCommandManager().getCommands();
            EmbedBuilder embed = new EmbedBuilder()
                    .setAuthor("Page " + page)
                    .setTitle("📄 - A List of all my commands - 📄")
                    .setColor(new Color(255, 255, 0))
                    .setTimestamp(null);
            for (int i = 25 * (page - 1); i < commands.size() && i != 25 * page; i++) {

                Command command = commands.get(i);
                CommandBuilder builder = command.getClass().getAnnotation(CommandBuilder.class);

                embed.addField(
                        "🎟️ - " + builder.name() + " - 🎟️",
                        "**- Usages:** " + Util.codeArrayToString(CommandManager.getUsages(event.getCommandManager().getBot().getPrefix(), builder)), true);
            }
            embed.setFooter("to see more info on a specific command do `" + event.getCommandManager().getBot().getPrefix() + "help <command name>`");
            event.getChannel().sendMessage(embed.build()).queue();
        } else {
            List<Command> commands = event.getCommandManager().getCommands();
            for (Command command : commands) {
                CommandBuilder builder = command.getClass().getAnnotation(CommandBuilder.class);

                if (builder.name().equals(event.getArg(0)) || Arrays.asList(builder.aliases()).contains(event.getArg(0))) {
                    String commandData = "🎟️ - " + builder.name() + " - 🎟️\n";
                    commandData+="**Usages :** " + Util.codeArrayToString(CommandManager.getUsages(event.getCommandManager().getBot().getPrefix(), builder)) + " \n";
                    commandData+="**Description :** " + builder.description() + " \n";
                    commandData+="**Aliases :** " + Util.codeArrayToString(builder.aliases()) + " \n";
                    event.getChannel().sendMessage(commandData).queue();
                    return;
                }
            }
            event.reply("That is not a valid command.").queue((result) -> result.delete().queueAfter(5, TimeUnit.SECONDS), Throwable::printStackTrace);
        }
    }
}
