package com.runningmanstudios.dankgamer.game.gambling;

public enum Suit {
    CLUBS("Clubs", "♣️"), HEARTS("Hearts", "♥️"), SPADES("Spades", "♠️"), DIAMONDS("Diamonds", "♦️"), WILD("Wild", "❔");

    private String name;
    private String icon;
    Suit(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getIcon() { return icon; }

    @Override
    public String toString() {
        return this.name;
    }
}
