package com.runningmanstudios.dankgamer.game.gambling.cards;

public enum HandScore {
    NOPAIRS(0),
    ONEPAIR(1),
    TWOPAIRS(2),
    THREEOFAKIND(3),
    STRAIGT(4),
    FLUSH(5),
    FULLHOUSE(6),
    FOUROFAKIND(7),
    STRAIGHTFLUSH(8),
    FIVEOFAKIND(9);

    int score;

    HandScore(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }
}
