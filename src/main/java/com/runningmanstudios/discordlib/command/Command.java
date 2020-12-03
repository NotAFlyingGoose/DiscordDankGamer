package com.runningmanstudios.discordlib.command;

import com.runningmanstudios.discordlib.event.CommandEvent;

public interface Command {
    void onMessage(CommandEvent event);
}
