package gametests;

public class Card {
    public static final int JOKER = 0;
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

    public String toIconString() {
        if (suit == Suit.WILD) return "\uD83C\uDCCF";

        return switch (number) {
            case 2 -> "2️⃣";
            case 3 -> "3️⃣";
            case 4 -> "4️⃣";
            case 5 -> "5️⃣";
            case 6 -> "6️⃣";
            case 7 -> "7️⃣";
            case 8 -> "8️⃣";
            case 9 -> "9️⃣";
            case 10 -> "\uD83D\uDD1F";
            case ACE -> "\uD83C\uDDE6";
            case JACK -> "⚜️";
            case QUEEN -> "\uD83D\uDC51\uD83D\uDC69";
            case KING -> "\uD83D\uDC51\uD83D\uDC68";
            default -> null;
        };

    }

    public String toCardString() {
        if (suit == Suit.WILD) return "Joker";

        if (number > 1 && number < 11) {
            return number + " of " + suit;
        }
        else if (number == ACE) {
            return "Ace of " + suit;
        }
        else if (number == JACK) {
            return "Jack of " + suit;
        }
        else if (number == QUEEN) {
            return "Queen of " + suit;
        }
        else if (number == KING) {
            return "King of " + suit;
        }

        return null;
    }

    @Override
    public String toString() {
        return toCardString();
    }

    @Override
    public Object clone() {
        return new Card(this.suit, this.number);
    }
}
