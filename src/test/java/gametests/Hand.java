package gametests;

import java.util.*;
import java.util.logging.Handler;

public class Hand {
    Deck deck;
    List<Card> cards = new ArrayList<>();

    public Hand(Deck deck) {
        this.deck = deck;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Hand collect() {
        for (int i = 0; i < 5; i++)
            cards.add(i, deck.takeCard());
        return this;
    }

    public Hand place() {
        for (Card c : cards)
            deck.putCard(c);
        cards.clear();
        return this;
    }

    public HandScore getScore() {
        // detecting pairs and cards of the same suit
        Set<Suit> suits = new HashSet<>();
        HashMap<Integer, Integer> pairs = new HashMap<>();
        Set<Integer> locked = new HashSet<>();
        int currentHighest = -1;
        for (Card card : cards) {
            if (currentHighest == -1) currentHighest = card.number;
            else if (card.number == currentHighest + 1) currentHighest++;
            else if (card.number > currentHighest) currentHighest = 100;

            suits.add(card.getSuit());

            System.out.println(card + ":");
            for (Card compare : cards) {
                if ((compare.number == card.number && compare.suit == card.suit) || !locked.contains(compare.number)) continue;

                System.out.println(compare);
                if (compare.number == card.number) {
                    if (pairs.containsKey(card.number))
                        pairs.replace(card.number, pairs.get(card.number) + 1);
                    else
                        pairs.put(card.number, 1);
                }
            }

            locked.add(card.number);
        }

        System.out.println(suits);
        System.out.println(pairs);

        // detect if the pairs are only two of the
        boolean purePairs = true;
        int totalPairs = 0;
        for (Integer card : pairs.keySet()) {
            totalPairs+=pairs.get(card);
            if (pairs.get(card) != 1) {
                purePairs = false;
            }
        }

        // Pairs
        if (!purePairs) {
            if (totalPairs == 2) return HandScore.FULLHOUSE;
            if (suits.size() == 1) return HandScore.FLUSH;
        }

        if (totalPairs == 5) return HandScore.FIVEOFAKIND;
        if (totalPairs == 4) return HandScore.FOUROFAKIND;
        if (totalPairs == 3) return HandScore.THREEOFAKIND;
        if (totalPairs == 2) return HandScore.TWOPAIRS;
        if (totalPairs == 1) return HandScore.ONEPAIR;

        // Straights
        if (currentHighest != 100) {
            if (suits.size() == 1) return HandScore.STRAIGHTFLUSH;
            else return HandScore.STRAIGT;
        }

        return HandScore.NOPAIRS;
    }
}
