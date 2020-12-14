package com.runningmanstudios.discordlib.data;

public class Item {
    private final String id;
    private final String name;
    private final String icon;
    private final int rarity;

    public Item(String id, String name, String icon, int rarity) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.rarity = rarity;
    }

    public String getRarityString() {
        return getRarityName() + " " + getRaritySymbol();
    }

    public String getRaritySymbol() {
        return switch (rarity) {
            case 1 -> "\uD83D\uDFE2";
            case 2 -> "\uD83D\uDD35";
            case 3 -> "\uD83D\uDD34";
            case 4 -> "\uD83D\uDFE3";
            case 5 -> "\uD83D\uDFE1";
            default -> "❓";
        };
    }

    public String getRarityName() {
        return switch (rarity) {
            case 1 -> "common";
            case 2 -> "uncommon";
            case 3 -> "rare";
            case 4 -> "epic";
            case 5 -> "legendary";
            default -> "unknown";
        };
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getRarity() {
        return rarity;
    }

    @Override
    public String toString() {
        return getClass().getName() + "[id=" + id + ", name=" + name + ", icon=" + icon + ", rarity=" + rarity + "]";
    }
}
