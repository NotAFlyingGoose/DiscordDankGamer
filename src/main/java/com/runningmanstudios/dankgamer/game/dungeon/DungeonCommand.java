package com.runningmanstudios.dankgamer.game.dungeon;

import com.runningmanstudios.dankgamer.game.GameCommand;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.CommandEvent;

@CommandBuilder(name = "dungeon",
        description = "travel through the darkest depths of a really cool dungeon. you can do add the word `restart` as a parameter to restart your game",
        usages = {"", "restart"})
public class DungeonCommand extends GameCommand<Dungeon> {
    @Override
    public Dungeon createNewGame(CommandEvent event) {
        return new Dungeon(event.getAuthor(), event);
    }

    @Override
    public String getGameNameNewLine() {
        return Dungeon.getFullGameNameNewLine();
    }
}