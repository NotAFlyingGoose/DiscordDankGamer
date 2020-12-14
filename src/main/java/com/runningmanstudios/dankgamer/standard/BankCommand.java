package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.data.MemberData;
import com.runningmanstudios.discordlib.event.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@CommandBuilder(name = "bank", description = "bank command", usages = {"", "<user>"})
public class BankCommand implements Command {
    @Override
    public void onMessage(CommandEvent message) {
        try {
            List<Member> members = message.getMessage().getMentionedMembers();
            if (members.isEmpty()) {
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(new Color(255, 0, 0))
                        .setAuthor("Bank Account")
                        .setTitle(message.getAuthor().getName()+"'s Bank Account")
                        .setThumbnail(message.getAuthor().getAvatarUrl())
                        .addField("\uD83D\uDCB8", String.valueOf(message.getMemberData().coins), true);
                // failure is always a Throwable
                message.getChannel().sendMessage(embed.build()).queue();
            } else {
                Member member = members.get(0);
                EmbedBuilder embed = new EmbedBuilder()
                        .setColor(new Color(255, 0, 0))
                        .setAuthor("Bank Account")
                        .setTitle(message.getAuthor().getName()+"'s Bank Account")
                        .addField("\uD83D\uDCB8", String.valueOf(message.getMemberData().coins), true);
                message.getChannel().sendMessage(embed.build()).queue();
            }
        } catch (NullPointerException e) {
            message.getChannel().sendMessage("I couldn't find the person you're looking for").queue((result) -> result.delete().queueAfter(5, TimeUnit.SECONDS), Throwable::printStackTrace);
        }
    }
}
