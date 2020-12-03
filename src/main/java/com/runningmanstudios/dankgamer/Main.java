package com.runningmanstudios.dankgamer;

import com.runningmanstudios.dankgamer.game.fishing.FishCommand;
import com.runningmanstudios.dankgamer.standard.*;
import com.runningmanstudios.discordlib.Bot;
import com.runningmanstudios.dankgamer.game.dungeon.DungeonCommand;

public class Main {
    public static void main(String[] args) {
        Bot bot = new Bot("/dankgamer/data/");
        bot.addCommand(new HelpCommand());
        bot.addCommand(new BankCommand());
        bot.addCommand(new LevelCommand());
        bot.addCommand(new AvatarCommand());
        bot.addCommand(new ShopCommand());
        bot.addCommand(new BuyCommand());
        bot.addCommand(new InvCommand());
        bot.addCommand(new DungeonCommand());
        bot.addCommand(new FishCommand());
    }
}
