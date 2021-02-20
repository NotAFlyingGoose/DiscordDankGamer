package com.runningmanstudios.discordlib.data;

import java.sql.*;
import java.util.HashSet;

public class MemoryDataBase {
    private static HashSet<MemberData> data;
    private static boolean started = false;

    public static void init() {
        if (started) return;
        started = true;
        data = new HashSet<>();
        System.out.println("Loaded DataBase.");
    }

    public static void addMemberData(String guild, String user) {
        for (MemberData memberData : data) {
            if (memberData.guildId.equals(guild) && memberData.userId.equals(user)) {
                data.add(new MemberData(user, guild, MemberData.DEFAULT_COINS, MemberData.DEFAULT_LEVEL, MemberData.DEFAULT_XP, MemberData.DEFAULT_INVENTORY, 0, 0, 0, null, 0, 0, null, null));
                return;
            }
        }
        throw new NoUserInDataBaseException(guild, user);
    }

    public static void updateMemberData(MemberData member) {
        for (MemberData memberData : data) {
            if (memberData.guildId.equals(member.guildId) && memberData.userId.equals(member.userId)) {
                data.remove(memberData);
                data.add(member);
                return;
            }
        }
        throw new NoUserInDataBaseException(member.guildId, member.userId);
    }

    public static void deleteMemberData(String guild, String user) {
        for (MemberData memberData : data) {
            if (memberData.guildId.equals(guild) && memberData.userId.equals(user)) {
                data.remove(memberData);
                return;
            }
        }
        throw new NoUserInDataBaseException(guild, user);
    }

    public static MemberData getMemberData(String guild, String user) {
        for (MemberData memberData : data) {
            if (memberData.guildId.equals(guild) && memberData.userId.equals(user)) {
                return memberData;
            }
        }
        throw new NoUserInDataBaseException(guild, user);
    }
}
