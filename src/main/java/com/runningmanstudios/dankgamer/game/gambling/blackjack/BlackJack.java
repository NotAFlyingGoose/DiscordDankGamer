package com.runningmanstudios.dankgamer.game.gambling.blackjack;

import com.runningmanstudios.dankgamer.game.gambling.cards.Card;
import com.runningmanstudios.dankgamer.game.gambling.cards.Deck;
import com.runningmanstudios.dankgamer.game.gambling.cards.Hand;
import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.ArrayList;
import java.util.List;

public class BlackJack {
    public static final int DEALERBUST = 4;
    public static final int WON = 3;
    public static final int TIE = 2;
    public static final int LOST = 1;
    public static final int NOTHING = 0;
    public final int bet;
    public final Deck deck = Deck.createStandardDeck().shuffle();
    public final Hand dealerHand = new Hand(deck, 2).collectAll();
    public final Hand playerHand = new Hand(deck, 2).collectAll();
    private boolean done = false;

    public BlackJack(int bet) {
        this.bet = bet;
    }

    public void sendMenu(int end, BotMessageEvent event) {
        EmbedBuilder menu = new EmbedBuilder();
        menu.setTitle("Blackjack");

        StringBuilder dsb = new StringBuilder();
        StringBuilder psb = new StringBuilder();


        if (end != BlackJack.NOTHING) {
            switch (end) {
                case BlackJack.DEALERBUST -> menu.setDescription("The dealer busts!\n**You Won!!! :star_struck:**");
                case BlackJack.WON -> menu.setDescription("**You Won!!! :star_struck:**");
                case BlackJack.TIE -> menu.setDescription("**Tie :frowning:\nNo one wins**");
                case BlackJack.LOST -> menu.setDescription("**Bust :frowning2:\nNo worries, you'll get em' next time :wink:**");
            }

            menu.setFooter(event.getAuthor().getAsTag() + " | Type \""+event.getBot().getPrefix() + "blackjack <bet amount>\" to play again!");
            for (Card card : dealerHand.getCards())
                dsb.append(card.toEmojiString()).append(" - ");
            dsb.delete(dsb.length() - 4, dsb.length());
        } else {
            menu.setDescription("Gambling Addiction is a serious problem, if you or anyone you know has a problem find help here: https://www.addictioncenter.com/drugs/gambling-addiction/.");
            menu.setFooter(event.getAuthor().getAsTag() + " | Type \"hit\" or \"stand\"");
            dsb.append(dealerHand.getCards().get(0).toEmojiString()).append(" - \uD83C\uDFB4");
        }

        for (Card card : playerHand.getCards())
            psb.append(card.toEmojiString()).append(" - ");

        psb.delete(psb.length() - 4, psb.length());

        menu.addField("Dealer's Hand", dsb.toString(), false);
        menu.addField("Your Hand", psb.toString(), false);

        event.getChannel().sendMessage(menu.build()).queue();
    }

    public void onResponse(BotMessageEvent event) {
        if ("hit".equals(event.getMessage().getContentRaw())) {
            playerHand.collectCard();

            if (hasBlackjack(playerHand)) {
                event.getCommandManager().stopAttracting(event.getAuthor());
                if (hasBlackjack(dealerHand)) {
                    sendMenu(BlackJack.TIE, event);
                    return;
                }
                Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), Util.getUserCoins(event.getGuild().getId(), event.getAuthor().getId()) + (int) (bet * 0.5));

                sendMenu(BlackJack.WON, event);

                return;
            }

            List<Integer> counts = getCounts(playerHand, false);

            if (counts.isEmpty()) {
                Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), Util.getUserCoins(event.getGuild().getId(), event.getAuthor().getId()) - bet);

                sendMenu(LOST, event);
                event.getCommandManager().stopAttracting(event.getAuthor());
                this.done = true;
            }
            else
                sendMenu(NOTHING, event);
            return;
        }
        if (!event.getMessage().getContentRaw().equals("stand")) {
            event.reply("Incorrect response").queue();
            return;
        }

        List<Integer> dc = getCounts(dealerHand, false);
        while (getMaxCount(dc) <= 16) {
            System.out.println(getMaxCount(dc));
            dealerHand.collectCard();
            dc = getCounts(dealerHand, false);
        }

        int results;

        if (dc.isEmpty())
            results = 3;
        else
            results = getResults();

        if (results == 3) {
            Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), Util.getUserCoins(event.getGuild().getId(), event.getAuthor().getId()) + bet);

            sendMenu(DEALERBUST, event);
            event.getCommandManager().stopAttracting(event.getAuthor());
        }
        else if (results == 2) {
            Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), Util.getUserCoins(event.getGuild().getId(), event.getAuthor().getId()) + bet);

            sendMenu(WON, event);
            event.getCommandManager().stopAttracting(event.getAuthor());
        }
        else if (results == 1) {
            sendMenu(TIE, event);
            event.getCommandManager().stopAttracting(event.getAuthor());
        }
        else {
            Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), Util.getUserCoins(event.getGuild().getId(), event.getAuthor().getId()) - bet);

            sendMenu(LOST, event);
            event.getCommandManager().stopAttracting(event.getAuthor());
        }
        this.done = true;
    }

    private int getMaxCount(List<Integer> counts) {
        int max = 0;

        for (Integer i : counts) {
            if (i > max) max = i;
        }

        return max;
    }

    public boolean hasBlackjack(Hand hand) {
        boolean ace = false;
        boolean ten = false;
        for (Card card : hand.getCards()) {
            if (card.getId() == Card.JACK || card.getId() == Card.QUEEN || card.getId() == Card.KING)
                ten = true;
            else if (card.getId() == Card.ACE)
                ace = true;
        }

        return ten && ace;
    }

    public boolean isDone() {
        return done;
    }

    public int getResults() {
        List<Integer> dealerCounts = removeUnder16(getCounts(dealerHand, false));
        List<Integer> playerCounts = getCounts(playerHand, false);

        boolean won = false;
        boolean tie = false;
        for (Integer pc : playerCounts) {
            for (Integer dc : dealerCounts) {
                if (pc > dc) {
                    won = true;
                }
                else if (pc.equals(dc)) {
                    tie = true;
                }
            }
        }
        if (won) return 2;
        if (tie) return 1;
        return 0;
    }

    private List<Integer> removeUnder16(List<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) <= 16)
                list.remove(i);
        }
        return list;
    }

    private List<Integer> getCounts(Hand hand, boolean forceAce11) {
        ArrayList<Integer> counts = new ArrayList<>();
        counts.add(0);
        System.out.println(hand.getCards());
        for (Card card : hand.getCards()) {
            if (card.getId() == Card.JACK || card.getId() == Card.QUEEN || card.getId() == Card.KING)
                addToCounts(counts, 10);
            else if (card.getId() == Card.ACE) {
                if (!forceAce11) {
                    ArrayList<Integer> elevenCounts = (ArrayList<Integer>) counts.clone();
                    addToCounts(elevenCounts, 11);
                    addToCounts(counts, 1);
                    counts.addAll(elevenCounts);
                } else
                    addToCounts(counts, 11);
            } else
                addToCounts(counts, card.getId());
        }

        counts.removeIf(count -> count > 21);
        return counts;
    }

    private void addToCounts(List<Integer> list, int amt) {
        for (int i = 0; i < list.size(); i++) {
            list.set(i, list.get(i) + amt);
        }
    }
}
