package com.runningmanstudios.discordlib.data;

import java.sql.*;

public class DataBase {
    public static String username;
    public static String password;
    public static String ipAddress;
    private static boolean started = false;

    public static void init() {
        if (started) return;
        started = true;

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            System.out.println("Connecting to SQL Server ... ");
            try (Connection connection = createConnection()) {

                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("""
                            IF NOT EXISTS (
                                    SELECT *
                                    FROM sys.databases
                                    WHERE name = 'DankGamer'
                                    )
                            BEGIN
                                CREATE DATABASE [DankGamer]
                            END;""");
                }

                try (Statement statement = connection.createStatement()) {
                    statement.executeUpdate("""
                            USE DankGamer;
                            IF NOT EXISTS (
                                    SELECT * 
                                    FROM sysobjects 
                                    WHERE NAME='Users' AND xtype='U'
                                    )
                            BEGIN
                                CREATE TABLE Users (
                                    id int IDENTITY(1,1) PRIMARY KEY,
                                    userid varchar(8000) NOT NULL,
                                    guildid varchar(8000) NOT NULL,
                                    coins int, 
                                    level int,
                                    xp int,
                                    inventory varchar(8000),
                                    
                                    game_dungeon_mode int,
                                    game_dungeon_rank int,
                                    game_dungeon_magic float,
                                    game_dungeon_monster_id varchar(8000),
                                    game_dungeon_monster_rank float,
                                    
                                    game_fishing_mode int,
                                    game_fishing_rod varchar(8000),
                                    game_fishing_location varchar(8000)
                                );
                            END;""");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Loaded DataBase.");
    }

    public static void addMemberData(String guild, String user) {
        try (Connection connection = createConnection()) {
            String code = "USE DankGamer; INSERT Users (userid, guildid, coins, level, xp, inventory) " + "VALUES (?, ?, ?, ?, ?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(code)) {
                statement.setString(1, user);
                statement.setString(2, guild);
                statement.setInt(3, 0);
                statement.setInt(4, 1);
                statement.setInt(5, 0);
                statement.setString(6, ";");
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0)
                    throw new NoUserInDataBaseException(guild, user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateMemberData(MemberData member) {
        try (Connection connection = createConnection()) {
            String code = """
                            USE DankGamer;
                            UPDATE Users 
                            SET coins = ?, 
                                level = ?, 
                                xp = ?, 
                                inventory = ?,
                                
                                game_dungeon_mode = ?,
                                game_dungeon_rank = ?,
                                game_dungeon_magic = ?,
                                game_dungeon_monster_id = ?,
                                game_dungeon_monster_rank = ?,
                                
                                game_fishing_mode = ?,
                                game_fishing_rod = ?,
                                game_fishing_location = ?
                            WHERE userid = ? AND guildid = ?""";
            try (PreparedStatement statement = connection.prepareStatement(code)) {
                statement.setInt(1, member.coins);
                statement.setInt(2, member.level);
                statement.setInt(3, member.xp);
                statement.setString(4, member.inventory);

                statement.setInt(5, member.game_dungeon_mode);
                statement.setInt(6, member.game_dungeon_rank);
                statement.setFloat(7, member.game_dungeon_magic);
                statement.setString(8, member.game_dungeon_monster_id);
                statement.setFloat(9, member.game_dungeon_monster_rank);

                statement.setInt(10, member.game_fishing_mode);
                statement.setString(11, member.game_fishing_rod);
                statement.setString(12, member.game_fishing_location);

                statement.setString(13, member.userId);
                statement.setString(14, member.guildId);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0)
                    throw new NoUserInDataBaseException(member.guildId, member.userId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMemberData(String guild, String user) {
        try (Connection connection = createConnection()) {
            String code = "USE DankGamer; DELETE FROM Users WHERE userid = ? AND guildid = ?;";
            try (PreparedStatement statement = connection.prepareStatement(code)) {
                statement.setString(1, user);
                statement.setString(2, guild);
                int rowsAffected = statement.executeUpdate();
                if (rowsAffected == 0)
                    throw new NoUserInDataBaseException(guild, user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static MemberData getMemberData(String guild, String user) {
        MemberData result = null;
        try (Connection connection = createConnection()) {
            String code = "USE DankGamer; SELECT * FROM Users WHERE userid = ? AND guildid = ?;";

            PreparedStatement statement = connection.prepareStatement(code);
            statement.setString(1, user);
            statement.setString(2, guild);
            ResultSet resultSet = statement.executeQuery();
            boolean next = resultSet.next();
            if (next) result = new MemberData(
                    resultSet.getString(2), // user
                    resultSet.getString(3), // guild
                    resultSet.getInt(4), // coins
                    resultSet.getInt(5), // level
                    resultSet.getInt(6), // xp
                    resultSet.getString(7), // inventory
                    resultSet.getInt(8), // game_dungeon_mode
                    resultSet.getInt(9), // game_dungeon_rank
                    resultSet.getFloat(10), // game_dungeon_magic
                    resultSet.getString(11), // game_dungeon_monster_id
                    resultSet.getFloat(12), // game_dungeon_monster_rank
                    resultSet.getInt(13), // game_fishing_mode
                    resultSet.getString(14), // game_fishing_rod
                    resultSet.getString(15) // game_fishing_location
                    );
            else
                throw new NoUserInDataBaseException(guild, user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Connection createConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlserver://"+ipAddress+";instance=MSSQLSERVER;encrypt=true;TrustServerCertificate=true;", username, password);
    }

    public static void setUsername(String username) {
        DataBase.username = username;
    }

    public static void setPassword(String password) {
        DataBase.password = password;
    }

    public static void setIP(String ipAddress) {
        DataBase.ipAddress = ipAddress;
    }
}
