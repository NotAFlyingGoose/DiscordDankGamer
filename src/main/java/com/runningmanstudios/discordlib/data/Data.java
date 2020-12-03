package com.runningmanstudios.discordlib.data;

public class Data {
    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    private final String name;
    private final Object value;
    public Data(String name, Object value) {
        this.name = name;
        this.value = value;
    }
}
