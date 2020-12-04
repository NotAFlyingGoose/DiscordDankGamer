package com.runningmanstudios.dankgamer.game;

import com.runningmanstudios.discordlib.Bot;
import com.runningmanstudios.discordlib.event.CommandEvent;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;

public abstract class GameInstance {
    private boolean running = false;
    protected User player;
    protected List<String> nextPatterns = new LinkedList<>();
    protected Message lastShown = null;

    public GameInstance(User player) {
        this.player = player;
    }

/*    public static String getFullGameNameNewLine() {
        return getFullGameName() + "\n";
    }

    public static String getFullGameName() {
        return getGameEmoji() + " " + getGameName() + " " + getGameEmoji();
    }

    public static String getGameName() {
        return "Game";
    }

    public static String getGameEmoji() {
        return ":video_game:";
    }*/

    public abstract void onResponse(CommandEvent event);

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if (!running) running = true;
    }

    public void stop() {
        if (running) running = false;
    }

    public List<String> getNextPatterns() {
        return nextPatterns;
    }

    public Message getLastShown() {
        return lastShown;
    }

    public abstract void removePlayerData(Bot bot);
}
