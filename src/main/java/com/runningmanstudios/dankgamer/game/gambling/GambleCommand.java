package com.runningmanstudios.dankgamer.game.gambling;

import com.runningmanstudios.dankgamer.game.GameCommand;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.CommandEvent;

@CommandBuilder(name = "gamble", description = "gambling game", usages = {"", "remove"})
public class GambleCommand extends GameCommand<GamblingGame> {
    public GambleCommand() {
        super("Gambling");
    }

    @Override
    public GamblingGame createNewGame(CommandEvent event) {
        return new GamblingGame(event.getAuthor(), event);
    }

    @Override
    public String getGameNameNewLine() {
        return GamblingGame.getFullGameNameNewLine();
    }
}
