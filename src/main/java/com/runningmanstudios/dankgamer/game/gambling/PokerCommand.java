package com.runningmanstudios.dankgamer.game.gambling;

import com.runningmanstudios.discordlib.command.AttractListener;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;

@CommandBuilder(name = "poker", description = "play poker!", usages = {"host", "join", "leave"})
public class PokerCommand implements Command, AttractListener {
    @Override
    public void onMessage(CommandEvent event) {
        EmbedBuilder menu = new EmbedBuilder();
        menu.setTitle("Poker")
                .setDescription("Gambling Addiction is a serious problem, if you or anyone you know has a problem find help here: https://www.addictioncenter.com/drugs/gambling-addiction/. Here is a test poker menu")
                .setFooter(event.getAuthor().getAsTag());

        Deck deck = Deck.createStandardDeck().shuffle();
        Hand hand = new Hand(deck, 5).collect();

        for (Card card : hand.getCards()) {
            menu.addField(card.toEmojiString(), "This card is a " + card.toCardString(), true);
        }

        menu.addField("Rank", hand.getScore().name(), false);

        event.getChannel().sendMessage(menu.build()).queue();

    }

    @Override
    public void onAttract(CommandEvent event) {

    }
}
