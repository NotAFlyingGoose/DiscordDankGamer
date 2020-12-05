package gametests;

public class Card {
    public static final int ACE = 1;
    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;

    public Suit suit;
    public int number;

    public Card(Suit suit, int number) {
        this.suit = suit;
        this.number = number;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Card card) && card.suit == this.suit && card.number == this.number;
    }

    public Suit getSuit() {
        return suit;
    }

    public String toCardString() {
        if (suit == Suit.WILD) return "Joker";

        if (number > 1 && number < 11) {
            return number + " of " + suit;
        }
        else if (number == 1) {
            return "Ace of " + suit;
        }
        else if (number == 11) {
            return "Jack of " + suit;
        }
        else if (number == 12) {
            return "Queen of " + suit;
        }
        else if (number == 13) {
            return "King of " + suit;
        }

        return null;
    }

    @Override
    public String toString() {
        return toCardString();
    }
}
