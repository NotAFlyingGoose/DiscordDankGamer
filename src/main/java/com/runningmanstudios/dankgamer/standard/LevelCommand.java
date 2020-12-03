package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandBuilder(name = "level", description = "level command", usages = {"", "<user>"})
public class LevelCommand implements Command {
    @Override
    public void onMessage(CommandEvent message) {
        try {
            List<Member> members = message.getMessage().getMentionedMembers();
            if (members.isEmpty()) {
                long xpToNextLvl = ((Number) message.getCommandManager().getBot().users.getSection(message.getAuthor().getId()).get("level")).longValue() * 150;
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(new Color(0, 255, 0))
                        .setAuthor("Level")
                        .setTitle(message.getAuthor().getName()+"'s Level")
                        .setThumbnail(message.getAuthor().getAvatarUrl())
                        .addField("Current Level \uD83D\uDD3C", message.getCommandManager().getBot().users.getSection(message.getAuthor().getId()).get("level").toString(), true)
                        .addField("Current Xp ✨", message.getCommandManager().getBot().users.getSection(message.getAuthor().getId()).get("xp").toString() + "/" + xpToNextLvl, true);
                // failure is always a Throwable
                message.getChannel().sendMessage(embed.build()).queue();
            } else {
                Member member = members.get(0);
                long xpToNextLvl = ((Number) message.getCommandManager().getBot().users.getSection(member.getUser().getId()).get("level")).longValue() * 150;
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(new Color(0, 255, 0))
                        .setAuthor("Level")
                        .setTitle(member.getUser().getName()+"'s Level")
                        .setThumbnail(member.getUser().getAvatarUrl())
                        .addField("Current Level \uD83D\uDD3C", message.getCommandManager().getBot().users.getSection(member.getUser().getId()).get("level").toString(), true)
                        .addField("Current Xp ✨", message.getCommandManager().getBot().users.getSection(member.getUser().getId()).get("xp").toString() + "/" + xpToNextLvl, true);
                // failure is always a Throwable
                message.getChannel().sendMessage(embed.build()).queue();
            }
        } catch (NullPointerException e) {
            message.getChannel().sendMessage("I couldn't find the person you're looking for").queue((result) -> result.delete().queueAfter(5, TimeUnit.SECONDS), Throwable::printStackTrace);
        }
    }
}
