package com.runningmanstudios.dankgamer.game.oregontrail;

import com.runningmanstudios.dankgamer.game.GameCommand;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.BotMessageEvent;

@CommandBuilder(name = "oregon-trail", description = "play the hit game from the 1971", usages = {"", "remove"})
public class OregonTrailCommand extends GameCommand<OregonTrailGame> {
    public OregonTrailCommand() {
        super("The Oregon Trail");
    }

    @Override
    public OregonTrailGame createNewGame(BotMessageEvent event) {
        return new OregonTrailGame(event.getGuild(), event.getAuthor(), event);
    }

    @Override
    public String getGameNameNewLine() {
        return OregonTrailGame.getFullGameNameNewLine();
    }
}
