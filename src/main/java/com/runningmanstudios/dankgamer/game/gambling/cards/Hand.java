package com.runningmanstudios.dankgamer.game.gambling.cards;

import java.util.*;

public class Hand {
    private Deck deck;
    private List<Card> cards = new ArrayList<>();
    private final int max;

    public Hand(Deck deck, int max) {
        this.deck = deck;
        this.max = max;
    }

    public int getMax() {
        return max;
    }

    public Deck getDeck() {
        return deck;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Hand collect() {
        if (!cards.isEmpty()) place();

        for (int i = 0; i < max; i++)
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
        if (max != 5) {
            throw new RuntimeException("The max must be 5 to get a score on this deck");
        }
        List<Card> tempCards = new ArrayList<>(cards);
        tempCards.sort(Comparator.comparingInt(Card::getId));

        // detecting pairs and cards of the same suit
        Set<Suit> suits = new HashSet<>();
        HashMap<Integer, Integer> pairs = new HashMap<>();
        Set<Integer> locked = new HashSet<>();

        boolean handHasWild = false;
        int currentHighest = -1;
        for (Card card : tempCards) {
            if (card.getSuit() == Suit.WILD) handHasWild = true;

            if (currentHighest == -1) currentHighest = card.getId();
            else if (card.getId() == currentHighest + 1) currentHighest++;
            else if (card.getId() > currentHighest) currentHighest = 100;

            suits.add(card.getSuit());

            for (Card compare : tempCards) {
                if (card == compare || locked.contains(compare.getId())) continue;

                if (compare.getId() == card.getId()) {
                    if (pairs.containsKey(card.getId()))
                        pairs.put(card.getId(), pairs.get(card.getId()) + 1);
                    else
                        pairs.put(card.getId(), 2);
                }
            }

            locked.add(card.getId());
        }

        // detect if the pairs are only two of the
        boolean purePairs = true;
        int totalPair = 0;
        for (Integer card : pairs.keySet()) {
            totalPair += pairs.get(card);
            if (pairs.get(card) != 2) {
                purePairs = false;
            }
        }

        if (suits.size() == 1 && currentHighest == 100) return HandScore.FLUSH;
        // Pairs
        if (!purePairs) {
            if (pairs.size() == 2) return HandScore.FULLHOUSE;
        } else {
            if (pairs.size() == 2) return HandScore.TWOPAIRS;
            if (pairs.size() == 1) return HandScore.ONEPAIR;
        }

        if (totalPair == 4 && handHasWild) return HandScore.FIVEOFAKIND;
        if (totalPair == 4) return HandScore.FOUROFAKIND;
        if (totalPair == 3) return HandScore.THREEOFAKIND;


        // Straights
        if (currentHighest != 100) {
            if (suits.size() == 1) return HandScore.STRAIGHTFLUSH;
            else return HandScore.STRAIGT;
        }

        return HandScore.NOPAIRS;
    }
}
