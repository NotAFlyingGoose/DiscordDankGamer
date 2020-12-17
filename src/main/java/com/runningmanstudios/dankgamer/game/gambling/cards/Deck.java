package com.runningmanstudios.dankgamer.game.gambling.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    List<Card> cards = new ArrayList<>();

    public static Deck createStandardDeck() {
        Deck res = new Deck();
        res.cards.add(new Card(Suit.CLUBS, Card.ACE));
        res.cards.add(new Card(Suit.CLUBS, 2));
        res.cards.add(new Card(Suit.CLUBS, 3));
        res.cards.add(new Card(Suit.CLUBS, 4));
        res.cards.add(new Card(Suit.CLUBS, 5));
        res.cards.add(new Card(Suit.CLUBS, 6));
        res.cards.add(new Card(Suit.CLUBS, 7));
        res.cards.add(new Card(Suit.CLUBS, 8));
        res.cards.add(new Card(Suit.CLUBS, 9));
        res.cards.add(new Card(Suit.CLUBS, 10));
        res.cards.add(new Card(Suit.CLUBS, Card.JACK));
        res.cards.add(new Card(Suit.CLUBS, Card.QUEEN));
        res.cards.add(new Card(Suit.CLUBS, Card.KING));

        res.cards.add(new Card(Suit.SPADES, Card.ACE));
        res.cards.add(new Card(Suit.SPADES, 2));
        res.cards.add(new Card(Suit.SPADES, 3));
        res.cards.add(new Card(Suit.SPADES, 4));
        res.cards.add(new Card(Suit.SPADES, 5));
        res.cards.add(new Card(Suit.SPADES, 6));
        res.cards.add(new Card(Suit.SPADES, 7));
        res.cards.add(new Card(Suit.SPADES, 8));
        res.cards.add(new Card(Suit.SPADES, 9));
        res.cards.add(new Card(Suit.SPADES, 10));
        res.cards.add(new Card(Suit.SPADES, Card.JACK));
        res.cards.add(new Card(Suit.SPADES, Card.QUEEN));
        res.cards.add(new Card(Suit.SPADES, Card.KING));

        res.cards.add(new Card(Suit.HEARTS, Card.ACE));
        res.cards.add(new Card(Suit.HEARTS, 2));
        res.cards.add(new Card(Suit.HEARTS, 3));
        res.cards.add(new Card(Suit.HEARTS, 4));
        res.cards.add(new Card(Suit.HEARTS, 5));
        res.cards.add(new Card(Suit.HEARTS, 6));
        res.cards.add(new Card(Suit.HEARTS, 7));
        res.cards.add(new Card(Suit.HEARTS, 8));
        res.cards.add(new Card(Suit.HEARTS, 9));
        res.cards.add(new Card(Suit.HEARTS, 10));
        res.cards.add(new Card(Suit.HEARTS, Card.JACK));
        res.cards.add(new Card(Suit.HEARTS, Card.QUEEN));
        res.cards.add(new Card(Suit.HEARTS, Card.KING));

        res.cards.add(new Card(Suit.DIAMONDS, Card.ACE));
        res.cards.add(new Card(Suit.DIAMONDS, 2));
        res.cards.add(new Card(Suit.DIAMONDS, 3));
        res.cards.add(new Card(Suit.DIAMONDS, 4));
        res.cards.add(new Card(Suit.DIAMONDS, 5));
        res.cards.add(new Card(Suit.DIAMONDS, 6));
        res.cards.add(new Card(Suit.DIAMONDS, 7));
        res.cards.add(new Card(Suit.DIAMONDS, 8));
        res.cards.add(new Card(Suit.DIAMONDS, 9));
        res.cards.add(new Card(Suit.DIAMONDS, 10));
        res.cards.add(new Card(Suit.DIAMONDS, Card.JACK));
        res.cards.add(new Card(Suit.DIAMONDS, Card.QUEEN));
        res.cards.add(new Card(Suit.DIAMONDS, Card.KING));

        return res;
    }

    public static Deck createStandardJokerDeck() {
        Deck res = new Deck();

        res.cards.add(new Card(Suit.WILD, Card.JOKER));
        res.cards.add(new Card(Suit.WILD, Card.JOKER));

        return res;
    }

    public Deck shuffle() {
        Collections.shuffle(cards);
        return this;
    }

    public List<Card> getCards() {
        return cards;
    }

    public Card takeCard() {
        if (cards.size() == 0) return null;
        Card res = cards.get(cards.size() - 1);
        cards.remove(cards.size() - 1);
        return res;
    }

    public void putCard(Card card) {
        if (cards.contains(card)) {
            System.err.println("The deck already contains that card");
            return;
        }
        cards.add(0, card);
    }
}
