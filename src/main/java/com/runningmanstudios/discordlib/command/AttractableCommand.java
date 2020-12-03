package com.runningmanstudios.discordlib.command;

import com.runningmanstudios.discordlib.event.CommandEvent;

public interface AttractableCommand extends Command {
    void onAttract(CommandEvent event);
}
