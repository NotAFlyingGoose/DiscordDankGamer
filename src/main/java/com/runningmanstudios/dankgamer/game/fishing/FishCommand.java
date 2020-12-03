package com.runningmanstudios.dankgamer.game.fishing;

import com.runningmanstudios.dankgamer.game.GameCommand;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.CommandEvent;

@CommandBuilder(name = "fish", description = "fishing game", usages = {"", "restart"})
public class FishCommand extends GameCommand<FishingGame> {
    @Override
    public FishingGame createNewGame(CommandEvent event) {
        return new FishingGame(event.getAuthor(), event);
    }

    @Override
    public String getGameNameNewLine() {
        return FishingGame.getFullGameNameNewLine();
    }
}
