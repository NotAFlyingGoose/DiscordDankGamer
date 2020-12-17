package com.runningmanstudios.dankgamer.game.gambling.poker;

import com.runningmanstudios.discordlib.command.*;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

@CommandBuilder(name = "poker", description = "play poker!", usages = {"list", "join <game id>", "leave", "host", "start"})
public class PokerCommand implements Command, AttractListener {
    Set<PokerGame> games = new HashSet<>();
    List<String> leaveRequests = new LinkedList<>();
    List<String> hostRequests = new LinkedList<>();
    HashMap<String, String> joinRequests = new HashMap<>();
    final String restartName = "Poker";

    @Override
    public void onAttract(BotMessageEvent event) {
        if (leaveRequests.contains(event.getAuthor().getId())) {
            PokerGame game = getGame(event.getAuthor());
            if (game == null) {
                event.reply("You are not a part of any game").queue();
                return;
            }
            if (event.getMessage().getContentRaw().equals("Y")) {
                game.requestLeave(event.getAuthor());
                if (game.getPlayers().isEmpty()) {
                    game.stop();
                    games.remove(game);
                }
                event.reply("Left game").queue();
                leaveRequests.remove(event.getAuthor().getId());
            } else {
                event.reply("Cancelled").queue();
            }
            event.getCommandManager().stopAttracting(event.getAuthor());
            return;
        }
        if (hostRequests.contains(event.getAuthor().getId())) {
            if (getGame(event.getAuthor()) != null) {
                event.reply("You are already a part of game #" + getGame(event.getAuthor()).getId()).queue();
            }

            if (event.getMessage().getContentRaw().equals("Y")) {
                PokerGame game = new PokerGame(event.getAuthor(), generateUniqueId());
                game.requestJoin(event.getCommandManager().getBot(), event.getAuthor(), event.getChannel());
                games.add(game);
                event.reply("You are now hosting game #" + game.getId()).queue();
                hostRequests.remove(event.getAuthor().getId());
            } else {
                event.reply("Cancelled").queue();
            }
            event.getCommandManager().stopAttracting(event.getAuthor());
        }
    }

    public static int generateUniqueId() {
        return Integer.parseInt(String.valueOf(UUID.randomUUID().toString().hashCode()).replaceAll("-", ""));
    }

    @Override
    public void onMessage(BotMessageEvent event) {
        switch (event.getArg(0)) {
            case "list" -> {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("Poker");
                embed.setFooter(event.getAuthor().getAsTag());
                for (PokerGame game : games) {
                    embed.addField(game.getHost().getName(), "id `" + game.getId() + "`", true);
                }
                event.getChannel().sendMessage(embed.build()).queue();
            }
            case "join" -> {
                if (getGame(event.getAuthor()) != null) {
                    event.reply("You are already in a game").queue();
                    return;
                }
                int requestedId = Integer.parseInt(event.getArg(1));
                for (PokerGame game : games) {
                    if (game.getId() == requestedId) {
                        if (!game.requestJoin(event.getCommandManager().getBot(), event.getAuthor(), event.getChannel())) {
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " was not accepted into the game").queue();
                        } else {
                            event.getChannel().sendMessage(event.getAuthor().getAsMention() + " was accepted into the game").queue();
                        }
                    }
                }
            }
            case "leave" -> {
                if (getGame(event.getAuthor()) == null) {
                    event.reply("You are not in a game").queue();
                    return;
                }
                event.reply("Are you sure you want to leave the game? (Y/N)").queue();
                event.getCommandManager().setAttractor(event.getAuthor(), AttractorFactory.createAnyAttractor(this));
                leaveRequests.add(event.getAuthor().getId());
            }
            case "host" -> {
                if (getGame(event.getAuthor()) != null) {
                    event.reply("You are already in a game").queue();
                    return;
                }
                event.reply("Are you sure you want to host a game? (Y/N)").queue();
                event.getCommandManager().setAttractor(event.getAuthor(), AttractorFactory.createAnyAttractor(this));
                hostRequests.add(event.getAuthor().getId());
            }
            case "start" -> {
                PokerGame game = getGame(event.getAuthor());
                if (game == null) {
                    event.reply("You are not in a game").queue();
                    return;
                }
                if (!game.getHost().getId().equals(event.getAuthor().getId())) {
                    event.reply("You are not the current host").queue();
                    return;
                }

                game.startLobby();
            }
        }

    }

    public PokerGame getGame(User user) {
        for (PokerGame game : games) {
            if (game.hasUser(user)) return game;
        }
        return null;
    }

}
