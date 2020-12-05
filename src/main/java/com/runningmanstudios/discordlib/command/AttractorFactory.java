package com.runningmanstudios.discordlib.command;

import java.util.regex.Pattern;

public class AttractorFactory {

    public static Attractor createAttractor(AttractListener listener, Pattern... patterns) {
        Attractor res = new Attractor(listener);
        for (Pattern p : patterns)
            res.addAnswer(p);
        return res;
    }

    public static Attractor createAttractor(AttractListener listener, String firstWord, String secondWord) {
        return new Attractor(listener).addAnswer(Pattern.compile(firstWord)).addAnswer(Pattern.compile(secondWord));
    }

    public static Attractor createNumberAttractor(AttractListener listener, int min, int max) {
        if (min > max)
            throw new IllegalArgumentException("min is greater than max");
        Attractor res = new Attractor(listener);
        for (int i = min; i < max; i++)
            res.addAnswer(Pattern.compile(String.valueOf(i)));
        return res;
    }

    public static Attractor createAnyAttractor(AttractListener listener) {
        return new Attractor(listener).addAnswer(Pattern.compile(".*"));
    }

    public static Attractor createYNAttractor(AttractListener listener) {
        return createAttractor(listener, "Y", "N");
    }

    public static Attractor createYesNoAttractor(AttractListener listener) {
        return createAttractor(listener, "Yes", "No");
    }

    public static Attractor createAcceptCancelAttractor(AttractListener listener) {
        return createAttractor(listener, "Accept", "Cancel");
    }

}
