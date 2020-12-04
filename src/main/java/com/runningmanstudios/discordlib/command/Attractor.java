package com.runningmanstudios.discordlib.command;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Attractor {

    private final List<Pattern> answers = new LinkedList<>();
    private final AttractListener listener;
    private Instant start;

    Attractor(AttractListener listener) {
        this.listener = listener;
        this.start = Instant.now();
    }

    public Attractor addAnswer(Pattern pattern) {
        answers.add(pattern);
        return this;
    }

    public boolean textEquals(String str) {
        String s = str.trim();
        for (Pattern pattern : answers) {
            Matcher m = pattern.matcher(s);
            if (m.matches()) return true;
        }
        return false;
    }

    public AttractListener getListener() {
        return listener;
    }

    public Instant getStart() {
        return start;
    }

    public void updateTime() {
        this.start = Instant.now();
    }
}
