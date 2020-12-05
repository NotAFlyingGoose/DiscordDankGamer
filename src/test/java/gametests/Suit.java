package gametests;

public enum Suit {
    CLUBS("Clubs"), HEARTS("Hearts"), SPADES("Spades"), DIAMONDS("Diamonds"), WILD("Wild");

    String name;
    Suit(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
