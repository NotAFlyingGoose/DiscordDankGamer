package com.runningmanstudios.dankgamer.game.fishing;

import com.runningmanstudios.dankgamer.game.GameCommand;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.CommandEvent;

@CommandBuilder(name = "fish", description = "fishing game", usages = {"", "remove"})
public class FishCommand extends GameCommand<FishingGame> {
    public FishCommand() {
        super("Fishing");
    }

    @Override
    public FishingGame createNewGame(CommandEvent event) {
        return new FishingGame(event.getAuthor(), event);
    }

    @Override
    public String getGameNameNewLine() {
        return FishingGame.getFullGameNameNewLine();
    }
}
