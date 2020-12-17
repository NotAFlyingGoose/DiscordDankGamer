package com.runningmanstudios.dankgamer.game.dungeon;

import com.runningmanstudios.dankgamer.game.GameCommand;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.BotMessageEvent;

@CommandBuilder(name = "dungeon",
        description = "travel through the darkest depths of a really cool dungeon. you can do add the word `restart` as a parameter to restart your game",
        usages = {"", "remove"})
public class DungeonCommand extends GameCommand<Dungeon> {
    public DungeonCommand() {
        super("Dungeon");
    }

    @Override
    public Dungeon createNewGame(BotMessageEvent event) {
        return new Dungeon(event.getGuild(), event.getAuthor(), event);
    }

    @Override
    public String getGameNameNewLine() {
        return Dungeon.getFullGameNameNewLine();
    }
}
