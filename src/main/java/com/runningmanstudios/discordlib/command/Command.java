package com.runningmanstudios.discordlib.command;

import com.runningmanstudios.discordlib.event.BotMessageEvent;

public interface Command {
    void onMessage(BotMessageEvent event);
}
