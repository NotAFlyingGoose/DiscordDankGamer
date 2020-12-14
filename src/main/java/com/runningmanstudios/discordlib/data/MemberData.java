package com.runningmanstudios.discordlib.data;

public class MemberData {
    // STANDARD DATA

    public final String userId;
    public final String guildId;
    public final int coins;
    public final int level;
    public final int xp;
    public final String inventory;

    // GAME DATA

    // DUNGEON
    public final int game_dungeon_mode;
    public final int game_dungeon_rank;
    public final float game_dungeon_magic;
    public final String game_dungeon_monster_id;
    public final float game_dungeon_monster_rank;

    // FISHING
    public final int game_fishing_mode;
    public final String game_fishing_rod;
    public final String game_fishing_location;

    public MemberData(String userId,
                      String guildId,
                      int coins,
                      int level,
                      int xp,
                      String inventory,
                      int game_dungeon_mode,
                      int game_dungeon_rank,
                      float game_dungeon_magic,
                      String game_dungeon_monster_id,
                      float game_dungeon_monster_rank,
                      int game_fishing_mode,
                      String game_fishing_rod,
                      String game_fishing_location) {
        this.userId = userId;
        this.guildId = guildId;
        this.coins = coins;
        this.level = level;
        this.xp = xp;
        this.inventory = inventory;

        this.game_dungeon_mode = game_dungeon_mode;
        this.game_dungeon_rank = game_dungeon_rank;
        this.game_dungeon_magic = game_dungeon_magic;
        this.game_dungeon_monster_id = game_dungeon_monster_id;
        this.game_dungeon_monster_rank = game_dungeon_monster_rank;

        this.game_fishing_mode = game_fishing_mode;
        this.game_fishing_rod = game_fishing_rod;
        this.game_fishing_location = game_fishing_location;
    }

    public MemberData withCoins(int coins) {
        return new MemberData(userId, guildId, coins, level, xp, inventory, game_dungeon_mode, game_dungeon_rank, game_dungeon_magic, game_dungeon_monster_id, game_dungeon_monster_rank, game_fishing_mode, game_fishing_rod, game_fishing_location);
    }

    public MemberData withLevel(int level) {
        return new MemberData(userId, guildId, coins, level, xp, inventory, game_dungeon_mode, game_dungeon_rank, game_dungeon_magic, game_dungeon_monster_id, game_dungeon_monster_rank, game_fishing_mode, game_fishing_rod, game_fishing_location);
    }

    public MemberData withXP(int xp) {
        return new MemberData(userId, guildId, coins, level, xp, inventory, game_dungeon_mode, game_dungeon_rank, game_dungeon_magic, game_dungeon_monster_id, game_dungeon_monster_rank, game_fishing_mode, game_fishing_rod, game_fishing_location);
    }

    public MemberData withInventory(String inventory) {
        return new MemberData(userId, guildId, coins, level, xp, inventory, game_dungeon_mode, game_dungeon_rank, game_dungeon_magic, game_dungeon_monster_id, game_dungeon_monster_rank, game_fishing_mode, game_fishing_rod, game_fishing_location);
    }

    public MemberData withDungeon(int game_dungeon_mode,
                                  int game_dungeon_rank,
                                  float game_dungeon_magic,
                                  String game_dungeon_monster_id,
                                  float game_dungeon_monster_rank) {
        return new MemberData(userId, guildId, coins, level, xp, inventory, game_dungeon_mode, game_dungeon_rank, game_dungeon_magic, game_dungeon_monster_id, game_dungeon_monster_rank, game_fishing_mode, game_fishing_rod, game_fishing_location);
    }
    public MemberData withFishing(int game_fishing_mode,
                                  String game_fishing_rod,
                                  String game_fishing_location) {
        return new MemberData(userId, guildId, coins, level, xp, inventory, game_dungeon_mode, game_dungeon_rank, game_dungeon_magic, game_dungeon_monster_id, game_dungeon_monster_rank, game_fishing_mode, game_fishing_rod, game_fishing_location);
    }
}
