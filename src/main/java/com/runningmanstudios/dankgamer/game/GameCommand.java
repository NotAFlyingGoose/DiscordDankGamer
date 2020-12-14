package com.runningmanstudios.dankgamer.game;

import com.runningmanstudios.discordlib.command.Attractor;
import com.runningmanstudios.discordlib.command.AttractorFactory;
import com.runningmanstudios.discordlib.command.AttractListener;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.event.CommandEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public abstract class GameCommand <T extends GameInstance> implements Command, AttractListener {
    Map<String, T> games = new HashMap<>();
    List<String> restartRequests = new LinkedList<>();
    final String restartName;

    protected GameCommand(String restartName) {
        this.restartName = restartName;
    }

    @Override
    public void onAttract(CommandEvent event) {
        if (restartRequests.contains(event.getAuthor().getId())) {
            if (event.getMessage().getContentRaw().equals("Remove " + restartName + " (" + event.getAuthor().getId() + ")")) {
                if (games.containsKey(event.getAuthor().getId())) {
                    games.get(event.getAuthor().getId()).removePlayerData(event.getCommandManager().getBot());
                    games.get(event.getAuthor().getId()).stop();
                    games.remove(event.getAuthor().getId());
                    event.reply(getGameNameNewLine() + restartName + " data removed.").queue();
                } else {
                    event.reply(getGameNameNewLine() + "You have no currently loaded game. If you do have a game but haven't loaded it, then load your game first before trying to remove it.").queue();
                }
            } else {
                event.reply(getGameNameNewLine() + "Remove Cancelled").queue();
            }
            event.getCommandManager().stopAttracting(event.getAuthor());
            restartRequests.remove(event.getAuthor().getId());
            return;
        }

        boolean skip = false;
        if (!games.containsKey(event.getAuthor().getId())) {
            games.put(event.getAuthor().getId(), createNewGame(event));
            skip=true;
        }

        if (skip) {
            Attractor attract = AttractorFactory.createAttractor(this);
            for (String pattern : games.get(event.getAuthor().getId()).getNextPatterns()) {
                attract.addAnswer(Pattern.compile(pattern));
            }
            event.getCommandManager().setAttractor(event.getAuthor(), attract);
            return;
        }

        games.get(event.getAuthor().getId()).onResponse(event);
        Attractor attract = AttractorFactory.createAttractor(this);
        for (String pattern : games.get(event.getAuthor().getId()).getNextPatterns()) {
            attract.addAnswer(Pattern.compile(pattern));
        }
        event.getCommandManager().setAttractor(event.getAuthor(), attract);
    }

    @Override
    public void onMessage(CommandEvent event) {
        if (event.getArgs().length == 1 && event.getArg(0).equals("remove")) {
            event.reply(getGameNameNewLine() + "Are you sure you want to remove your game data? there is no reversing this. Type `"+"Remove " + restartName + " (" + event.getAuthor().getId() + ")"+"` to confirm.").queue();
            event.getCommandManager().setAttractor(event.getAuthor(), AttractorFactory.createAnyAttractor(this));
            restartRequests.add(event.getAuthor().getId());
        } else if (event.getArgs().length == 0) {
            if (!games.containsKey(event.getAuthor().getId()) || !games.get(event.getAuthor().getId()).isRunning()) {
                event.reply(getGameNameNewLine() + "Would you like to start/load a game? (Y/N)").queue();
                event.getCommandManager().setAttractor(event.getAuthor(), AttractorFactory.createAttractor(this, Pattern.compile("Y")));
                return;
            }

            event.getChannel().sendMessage(games.get(event.getAuthor().getId()).getLastShown()).queue();
            event.reply("Please answer with one of the following: " + games.get(event.getAuthor().getId()).getNextPatterns()).queue();
            Attractor attract = AttractorFactory.createAttractor(this);
            for (String pattern : games.get(event.getAuthor().getId()).getNextPatterns()) {
                attract.addAnswer(Pattern.compile(pattern));
            }
            event.getCommandManager().setAttractor(event.getAuthor(), attract);
        } else {
            throw new RuntimeException("User " + event.getAuthor().toString() + " typed too many arguments.");
        }
    }

    public abstract T createNewGame(CommandEvent event);

    public abstract String getGameNameNewLine();
}
