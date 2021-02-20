package com.runningmanstudios.discordlib.data;

import com.runningmanstudios.discordlib.DiscordBot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Inventory {
    HashMap<Item, Integer> items = new HashMap<>();

    public Inventory(DiscordBot bot, String data) {
        if (data == null || data.isEmpty() || data.equals(";")) return;

        List<String> items = new ArrayList<>(Arrays.asList(data.split(";", 0)));
        items.removeAll(Arrays.asList("", null));

        for (String info : items) {
            this.items.put(bot.getItem(info.split(":")[0]), Integer.parseInt(info.split(":")[1]));
        }
    }

    public Inventory give(Item item, int amount) {
        if (items.containsKey(item))
            items.replace(item, amount + items.get(item));
        else
            items.put(item, amount);
        return this;
    }

    public Inventory take(Item item, int amount) {
        if (items.containsKey(item))
            items.replace(item, amount - items.get(item));
        if (items.get(item) < 1)
            items.remove(item);
        return this;
    }

    public String toDataString() {
        StringBuilder sb = new StringBuilder(";");
        for (Item item : items.keySet()) {
            sb.append(item.getId()).append(":").append(items.get(item)).append(";");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return toDataString();
    }

    public boolean hasItem(String id) {
        for (Item item : items.keySet()) {
            if (item.getId().equals(id)) return true;
        }
        return false;
    }

    public HashMap<Item, Integer> getItems() {
        return this.items;
    }
}
