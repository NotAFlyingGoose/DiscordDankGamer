package com.runningmanstudios.discordlib;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;

public class Util {
    public static String arrayToString(Object[] array) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            sb.append(array[i].toString());
            if (i != array.length - 1) sb.append(", ");
        }

        return sb.toString();
    }

    public static String codeArrayToString(Object[] array) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < array.length; i++) {
            sb.append('`').append(array[i].toString()).append('`');
            if (i != array.length - 1) sb.append(", ");
        }

        return sb.toString();
    }

    public static MessageEmbed createSimpleEmbed(String title, String description) {
        return createSimpleEmbed(null, title, description, null, null);
    }

    public static MessageEmbed createSimpleEmbed(String title, String description, String footer) {
        return createSimpleEmbed(null, title, description, footer, null);
    }

    public static MessageEmbed createSimpleEmbed(String author, String title, String description, String footer, Color color) {
        EmbedBuilder embed = new EmbedBuilder();
        if (author != null)
            embed.setAuthor(author);
        if (footer != null)
            embed.setFooter(footer);
        if (color != null)
            embed.setColor(color);
        embed.setTitle(title);
        embed.setDescription(description);
        return embed.build();
    }
}
