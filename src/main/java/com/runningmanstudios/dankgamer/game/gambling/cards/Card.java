package com.runningmanstudios.dankgamer.game.gambling.cards;

public class Card {
    public static final int JOKER = 0;
    public static final int ACE = 1;
    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;

    private Suit suit;
    private int id;

    public Card(Suit suit, int id) {
        this.suit = suit;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Card card) && card.suit == this.suit && card.id == this.id;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getId() {
        return id;
    }

    public String getIcon() {
        if (suit == Suit.WILD) return "\uD83C\uDCCF";

        return switch (id) {
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
            case JACK -> "\uD83C\uDDEF";
            case QUEEN -> "\uD83D\uDC51\uD83D\uDC69";
            case KING -> "\uD83D\uDC51\uD83D\uDC68";
            default -> null;
        };
    }

    public String toEmojiString() {
        if (suit == Suit.WILD) return "\uD83C\uDCCF";

        return getIcon() + suit.getIcon();
    }

    public String toServerEmojiString() {
        return ":" + toCardString().toLowerCase().replace(" ", "_") + ":";
    }

    public String toCardString() {
        if (suit == Suit.WILD) return "Joker";

        if (id > 1 && id < 11) {
            return id + " of " + suit;
        }
        else if (id == ACE) {
            return "Ace of " + suit;
        }
        else if (id == JACK) {
            return "Jack of " + suit;
        }
        else if (id == QUEEN) {
            return "Queen of " + suit;
        }
        else if (id == KING) {
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
        return new Card(this.suit, this.id);
    }
}
