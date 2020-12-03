package com.runningmanstudios.discordlib.command;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AttractInfo {
    private final List<Pattern> answers = new LinkedList<>();
    private final AttractableCommand command;
    private Instant start;

    public AttractInfo(AttractableCommand command) {
        this.command = command;
        this.start = Instant.now();
    }

    public AttractInfo addAnswer(Pattern pattern) {
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

    public AttractableCommand getCommand() {
        return command;
    }

    public Instant getStart() {
        return start;
    }

    public void updateTime() {
        this.start = Instant.now();
    }
}
