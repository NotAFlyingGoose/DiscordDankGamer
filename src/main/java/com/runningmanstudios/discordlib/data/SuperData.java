package com.runningmanstudios.discordlib.data;

import org.json.simple.JSONObject;

public class SuperData extends Data {
    public SuperData(String name) {
        super(name, new JSONObject());
    }

    public SuperData addData(Data data) {
        ((JSONObject)this.getValue()).put(data.getName(), data.getValue());
        return this;
    }
}
