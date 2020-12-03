package com.runningmanstudios.dankgamer.game.dungeon;

import com.runningmanstudios.discordlib.event.CommandManager;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;

public class DungeonFighter {
    private final String name;
    private final float rank;
    private final float magic;
    public DungeonFighter(String name, float rank, float magic) {
        this.name = name;
        this.rank = rank;
        this.magic = magic;
    }

    public String getName() {
        return name;
    }

    public float getRank() {
        return rank;
    }

    public float getMagic() {
        return magic;
    }

    public static DungeonFighter createFromUser(User user, CommandManager commandManager) {
        JSONObject userData = commandManager.getBot().getUserData(user);
        JSONObject dungeonData = (JSONObject) userData.get("dungeon");
        return new DungeonFighter(user.getName(), ((Number)dungeonData.get("rank")).floatValue(), ((Number)dungeonData.get("magic")).floatValue());
    }
}
