package com.runningmanstudios.dankgamer.game.gambling.blackjack;

import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.command.AttractListener;
import com.runningmanstudios.discordlib.command.AttractorFactory;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.data.MemberData;
import com.runningmanstudios.discordlib.event.BotMessageEvent;

import java.util.HashMap;

@CommandBuilder(name = "blackjack", description = "Play blackjack! remember not to count the cards :)", usages = "<bet amount>", aliases = "bj")
public class BJCommand implements Command, AttractListener {

    private static final int MIN_BET = 25;
    private static final int MAX_BET = 500;
    HashMap<String, BlackJack> games = new HashMap<>();

    @Override
    public void onAttract(BotMessageEvent event) {
        BlackJack game = games.get(event.getAuthor().getId());
        game.onResponse(event);
        if (game.isDone())
            games.remove(event.getAuthor().getId());
    }

    @Override
    public void onMessage(BotMessageEvent event) {
        if (!games.containsKey(event.getAuthor().getId())) {
            int betting = Integer.parseInt(event.getArg(0));
            if (betting < BJCommand.MIN_BET || betting > BJCommand.MAX_BET) {
                event.reply("The minimum you can bet is " + BJCommand.MIN_BET + ", and the max is " + BJCommand.MAX_BET).queue();
                return;
            }
            int userCoins = Util.getUserCoins(event.getGuild().getId(), event.getAuthor().getId());
            if (userCoins < betting) {
                event.reply("You don't have the funds to play :frowning2:\nMaybe you should come back later").queue();
                return;
            }

            BlackJack game = new BlackJack(betting);
            if (game.hasBlackjack(game.playerHand)) {
                if (game.hasBlackjack(game.dealerHand)) {
                    game.sendMenu(BlackJack.TIE, event);
                    return;
                }
                Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), (int) (userCoins + (betting * 1.5)));

                game.sendMenu(BlackJack.WON, event);
                return;
            }
            games.put(event.getAuthor().getId(), game);
            game.sendMenu(BlackJack.NOTHING, event);
            event.getCommandManager().setAttractor(event.getAuthor(), AttractorFactory.createAnyAttractor(this));
        }
    }

}
