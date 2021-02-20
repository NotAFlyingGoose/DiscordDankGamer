package com.runningmanstudios.discordlib;

import com.runningmanstudios.discordlib.data.SQLDataBase;
import com.runningmanstudios.discordlib.data.NoUserInDataBaseException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Util {

    public static int getUserCoins(String guildId, String userId) {
        int result = 0;
        try (Connection connection = SQLDataBase.createConnection()) {
            String code = "USE DankGamer; SELECT coins FROM Users WHERE userid = ? AND guildid = ?;";

            PreparedStatement statement = connection.prepareStatement(code);
            statement.setString(1, userId);
            statement.setString(2, guildId);
            ResultSet resultSet = statement.executeQuery();
            boolean next = resultSet.next();
            if (next)
                result = resultSet.getInt(1);
            else
                throw new NoUserInDataBaseException(guildId, userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void setUserCoins(String guildId, String userId, int newValue) {
        String code = """
                        USE DankGamer;
                        UPDATE Users SET coins = ? WHERE userid = ? AND guildid = ?;""";
        try (Connection connection = SQLDataBase.createConnection();
             PreparedStatement statement = connection.prepareStatement(code)) {
            statement.setInt(1, newValue);
            statement.setString(2, userId);
            statement.setString(3, guildId);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0)
                throw new NoUserInDataBaseException(guildId, guildId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

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
