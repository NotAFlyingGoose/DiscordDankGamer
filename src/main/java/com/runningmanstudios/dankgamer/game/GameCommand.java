package com.runningmanstudios.dankgamer.game;

import com.runningmanstudios.dankgamer.game.fishing.FishingGame;
import com.runningmanstudios.discordlib.command.AttractInfo;
import com.runningmanstudios.discordlib.command.AttractableCommand;
import com.runningmanstudios.discordlib.event.CommandEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class GameCommand <T extends GameInstance> implements AttractableCommand {
    Map<String, T> games = new HashMap<>();
    List<String> restartRequests = new LinkedList<>();

    @Override
    public void onAttract(CommandEvent event) {
        if (event.getArgs().length!=0 && event.getArg(0).equals("restart")) {
            if (games.containsKey(event.getAuthor().getId())) {
                games.remove(event.getAuthor().getId());
                event.reply(FishingGame.getFullGameNameNewLine() + "Are you sure you want to restart your game, there is no reversing this? (Type `Restart Fishing (" + event.getAuthor().getId() + ")` to confirm)").queue();
                event.getCommandManager().setAttractor(event.getAuthor(), new AttractInfo(this).addAnswer(Pattern.compile(".*")));
                restartRequests.add(event.getAuthor().getId());
            } else {
                event.reply("You have no current game").queue();
            }
            return;
        }

        boolean skip = false;
        if (!games.containsKey(event.getAuthor().getId())) {
            games.put(event.getAuthor().getId(), createNewGame(event));
            skip=true;
        }

        if (skip) {
            AttractInfo attract = new AttractInfo(this);
            for (String pattern : games.get(event.getAuthor().getId()).getNextPatterns()) {
                attract.addAnswer(Pattern.compile(pattern));
            }
            event.getCommandManager().setAttractor(event.getAuthor(), attract);
            return;
        }

        games.get(event.getAuthor().getId()).onResponse(event);
        AttractInfo attract = new AttractInfo(this);
        for (String pattern : games.get(event.getAuthor().getId()).getNextPatterns()) {
            attract.addAnswer(Pattern.compile(pattern));
        }
        event.getCommandManager().setAttractor(event.getAuthor(), attract);
    }

    @Override
    public void onMessage(CommandEvent event) {
        if (event.getArgs().length!=0 && event.getArg(0).equals("restart")) {
            event.reply(getGameNameNewLine() + "Are you sure you want to restart your game, there is no reversing this? (Type `Restart Game ("+event.getAuthor().getId()+")` to confirm)").queue();
            event.getCommandManager().setAttractor(event.getAuthor(), new AttractInfo(this).addAnswer(Pattern.compile(".*")));
            restartRequests.add(event.getAuthor().getId());
            return;
        }

        if (!games.containsKey(event.getAuthor().getId()) || !games.get(event.getAuthor().getId()).isRunning()) {
            event.reply(getGameNameNewLine() + "Would you like to start/continue a game? (Y/N)").queue();
            event.getCommandManager().setAttractor(event.getAuthor(), new AttractInfo(this).addAnswer(Pattern.compile("Y")));
            return;
        }

        event.getChannel().sendMessage(games.get(event.getAuthor().getId()).getLastShown()).queue();
        event.reply("Please answer with one of the following: " + games.get(event.getAuthor().getId()).getNextPatterns()).queue();
        AttractInfo attract = new AttractInfo(this);
        for (String pattern : games.get(event.getAuthor().getId()).getNextPatterns()) {
            attract.addAnswer(Pattern.compile(pattern));
        }
        event.getCommandManager().setAttractor(event.getAuthor(), attract);
    }

    public abstract T createNewGame(CommandEvent event);

    public abstract String getGameNameNewLine();
}
