package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.data.MemberData;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandBuilder(name = "level", description = "level command", usages = {"", "<user>"})
public class LevelCommand implements Command {
    @Override
    public void onMessage(BotMessageEvent message) {
        try {
            List<Member> members = message.getMessage().getMentionedMembers();
            if (members.isEmpty()) {
                MemberData data = message.getMemberData();
                long xpToNextLvl = data.level * 175;
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(new Color(0, 255, 0))
                        .setAuthor("Level")
                        .setTitle(message.getAuthor().getName()+"'s Level")
                        .setThumbnail(message.getAuthor().getAvatarUrl())
                        .addField("Current Level \uD83D\uDD3C", String.valueOf(data.level), true)
                        .addField("Current Xp ✨", data.xp + "/" + xpToNextLvl, true);
                message.getChannel().sendMessage(embed.build()).queue();
            } else {
                Member member = members.get(0);
                MemberData data = DataBase.getMemberData(member.getGuild().getId(), member.getUser().getId());
                long xpToNextLvl = data.level * 175;
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(new Color(0, 255, 0))
                        .setAuthor("Level")
                        .setTitle(member.getUser().getName()+"'s Level")
                        .setThumbnail(member.getUser().getAvatarUrl())
                        .addField("Current Level \uD83D\uDD3C", String.valueOf(data.level), true)
                        .addField("Current Xp ✨", data.xp + "/" + xpToNextLvl, true);
                message.getChannel().sendMessage(embed.build()).queue();
            }
        } catch (NullPointerException e) {
            message.getChannel().sendMessage("I couldn't find the person you're looking for").queue((result) -> result.delete().queueAfter(5, TimeUnit.SECONDS), Throwable::printStackTrace);
        }
    }
}
