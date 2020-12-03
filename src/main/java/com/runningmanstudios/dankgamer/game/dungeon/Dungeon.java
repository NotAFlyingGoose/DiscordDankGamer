package com.runningmanstudios.dankgamer.game.dungeon;

import com.runningmanstudios.dankgamer.game.GameInstance;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.event.CommandEvent;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Dungeon extends GameInstance {
    private final static String gameName = "Dungeon";
    private final static String gameEmoji = ":european_castle:";

    public Dungeon(User player, CommandEvent event) {
        super(player);

        nextPatterns.add("continue");
        start();
        JSONObject userData = event.getCommandManager().getBot().getUserData(event.getAuthor());
        if (!userData.containsKey("dungeon")) {
            JSONObject game = new JSONObject();
            game.put("rank", 1);
            game.put("magic", 2);
            game.put("monster", new JSONArray());
            game.put("mode", -2);
            userData.put("dungeon", game);

            event.getCommandManager().getBot().users.writeContent();
        } else {
            JSONObject previousGame = (JSONObject) userData.get("dungeon");
            if (((Number) previousGame.get("mode")).intValue()>=100 || ((Number) previousGame.get("mode")).intValue()<1) {
                previousGame.put("rank", 1);
                previousGame.put("magic", 2);
                previousGame.put("monster", new JSONArray());
                previousGame.put("mode", -2);
                userData.put("dungeon", previousGame);

                event.getCommandManager().getBot().users.writeContent();
            } else {
                previousGame.put("mode", 1);
                userData.put("dungeon", previousGame);

                event.getCommandManager().getBot().users.writeContent();
                String menu = """
                ```md
                # type `continue` to continue your game...
                ```
                """;
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);

                event.getCommandManager().getBot().users.writeContent();
                return;
            }
        }

        String menu = """
                ```md
                < you are walking around in medieval york city when a man stops you and says >
                # "Pardon me, adventurer. I'm relieved to see you. I need some coochie right now, my stocks have run low and I need it now! if I don't get coochie then I'll die!!! Here is some magic so you know I'm not kidding around, please go get me some coochie!"
                < you go to find some coochie for the mysterious man. but when you're entering hooters you fall into a ditch, you drop your bag of coins and land in the sewer. eventually you find your way out but now your in the middle of nowhere >
                < As you walk through the forrest, you find yourself a comfortable log, take a bottle of lotion and the bible from your pouch, but then you hear a creak and swing around to see a giant stone gate. inside it is very dark and there are bat dropping everywhere. eventually, through all the corridors you find a giant wooden door. you are about to leave when the door opens >
                
                
                # type `continue` to continue story...
                ```
                """;
        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
    }

    public void onResponse(CommandEvent event) {
        nextPatterns.clear();
        JSONObject userData = event.getCommandManager().getBot().getUserData(event.getAuthor());
        JSONObject dungeonData = (JSONObject) userData.get("dungeon");
        JSONObject monsters = event.getCommandManager().findJSON("data.json").getSection("monsters");
        int mode = ((Number) dungeonData.get("mode")).intValue();
        float rank = ((Number) dungeonData.get("rank")).floatValue();
        float magic = ((Number) dungeonData.get("magic")).floatValue();
        String showMenu = "";
        switch (mode) {
            case -2 -> {
                String menu = createFightMenu(new DungeonFighter("Skella-Bone - 🦴", 1, 0), DungeonFighter.createFromUser(player, event.getCommandManager()), getSays("Skella-Bone", "*rattle*"), new String[]{"next"});
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                dungeonData.put("mode", mode + 1);
                nextPatterns.add("1");
                showMenu = menu;
            }
            case -1 -> {
                String menu = createFightMenu(new DungeonFighter("Skella-Bone - 🦴", 1, 0), DungeonFighter.createFromUser(player, event.getCommandManager()), "The monster has the same level as you, so you can not fight him normally. you have to use your magic\nyou can use magic instead of rank to kill a monster, but afterwards you lose some magic", new String[]{"fight"});
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                dungeonData.put("mode", mode + 1);
                nextPatterns.add("1");
                showMenu = menu;
            }
            case 0 -> {
                dungeonData.put("rank", rank+1);
                dungeonData.put("magic", magic-1);
                String menu = createFightMenu(new DungeonFighter("Skella-Bone - 🦴", 1, 0), DungeonFighter.createFromUser(player, event.getCommandManager()), "you kill the Skella-Bone\nyou killed the monster but lost some magic, because you beat this monster, you up your rank,\nsometimes, you can't beat a monster, with or without magic, so just go away for a bit and you will gain magic from just chatting\n", new String[]{"continue through dungeon"});
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                dungeonData.put("mode", mode + 1);
                nextPatterns.add("1");
                showMenu = menu;
            }
            case 1 -> {
                String monsterName = "";
                float monsterRank = 0;
                if (((JSONArray)dungeonData.get("monster")).size()<2) {
                    int monstersLength = monsters.size();
                    double monsterIndex = (Math.floor(Math.random() * monstersLength));
                    int i = 0;
                    for (Object monster : monsters.keySet()) {
                        if (i == monsterIndex) {
                            monsterName = monster.toString();
                            break;
                        }
                        i++;
                    }
                } else {
                    monsterName = ((JSONArray)dungeonData.get("monster")).get(0).toString();
                    monsterRank = ((Number)((JSONArray)dungeonData.get("monster")).get(1)).floatValue();
                }
                DungeonFighter player = DungeonFighter.createFromUser(this.player, event.getCommandManager());

                JSONObject monsterData = (JSONObject)monsters.get(monsterName);
                String monsterLogo = monsterData.get("logo").toString();
                String monsterSays = monsterData.get("says").toString()
                        .replace("${said}", event.getMessage().getContentRaw())
                        .replace("${name}", event.getAuthor().getName());
                float monsterBase = ((Number)monsterData.get("base")).floatValue();
                if (((JSONArray)dungeonData.get("monster")).size()<2) {
                    int min = (int) (player.getRank()-2);
                    int max = (int) (player.getRank()+7);
                    monsterRank = Math.max(1, Math.max(min, monsterBase + (float) (new Random().nextInt(max - min) + min)));
                }
                DungeonFighter monster = new DungeonFighter(monsterName+" - "+monsterLogo, monsterRank, 0);

                String menu = createFightMenu(monster, player, getSays(monsterName, monsterSays), new String[]{"fight", "use magic"});
                showMenu = menu;

                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                JSONArray monsterSave = new JSONArray();
                monsterSave.add(monsterName);
                monsterSave.add(monsterRank);

                dungeonData.put("monster", monsterSave);
                dungeonData.put("mode", mode+1);

                nextPatterns.add("1");
                nextPatterns.add("2");
            }
            case 2 -> {
                JSONArray fighting = (JSONArray) dungeonData.get("monster");
                String monsterName = fighting.get(0).toString();
                float monsterRank = ((Number)fighting.get(1)).floatValue();
                JSONObject monsterData = (JSONObject)monsters.get(monsterName);
                String monsterLogo = monsterData.get("logo").toString();

                DungeonFighter monster = new DungeonFighter(monsterName+" - "+monsterLogo, monsterRank, 0);
                DungeonFighter player = DungeonFighter.createFromUser(this.player, event.getCommandManager());

                int option = Integer.parseInt(event.getMessage().getContentRaw());
                if (option==1) {
                    if (monsterRank<player.getRank()) {
                        String menu = createFightMenu(monster, player, "you killed the " + monsterName, new String[]{"claim treasure"});
                        showMenu = menu;
                        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                        dungeonData.put("rank", rank+1);
                        dungeonData.put("magic", magic+0.2);
                        dungeonData.put("mode", mode+1);

                        nextPatterns.add("1");
                    }
                    else {
                        String menu = createFightMenu(monster, player, "The monsters is too good for your filthy \"rank\"", new String[]{"fight", "use magic"});
                        if (monsterData.get("onRank")!=null) menu = createFightMenu(monster, player, "The monsters is too good for your filthy \"rank\". It says, \""+monsterData.get("onRank").toString().replace("${said}", event.getMessage().getContentRaw()).replace("${name}", event.getAuthor().getName())+"\"", new String[]{"fight", "use magic"});
                        showMenu = menu;
                        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);

                        nextPatterns.add("1");
                        nextPatterns.add("2");
                    }
                } else if (option==2) {
                    if (monsterRank<player.getMagic()) {
                        String menu = createFightMenu(monster, player, "you killed the " + monsterName, new String[]{"claim treasure"});
                        showMenu = menu;
                        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                        dungeonData.put("rank", rank+1);
                        dungeonData.put("magic", magic-(monster.getRank()/2));
                        dungeonData.put("mode", mode+1);

                        nextPatterns.add("1");
                    }
                    else {
                        String menu = createFightMenu(monster, player, "The monster is stronger than your weak magic", new String[]{"fight", "use magic"});
                        if (monsterData.get("onMagic")!=null) menu = createFightMenu(monster, player, "The monster is stronger than your magic. It says, \""+monsterData.get("onMagic").toString().replace("${said}", event.getMessage().getContentRaw()).replace("${name}", event.getAuthor().getName())+"\"", new String[]{"fight", "use magic"});
                        showMenu = menu;
                        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);

                        nextPatterns.add("1");
                        nextPatterns.add("2");
                    }
                }
            }
            case 3 -> {
                JSONArray fighting = (JSONArray) dungeonData.get("monster");
                String monsterName = fighting.get(0).toString();
                float monsterRank = ((Number)fighting.get(1)).floatValue();
                JSONObject monsterData = (JSONObject)monsters.get(monsterName);
                String monsterLogo = monsterData.get("logo").toString();
                String monsterSays = monsterData.get("says").toString()
                        .replace("${said}", event.getMessage().getContentRaw())
                        .replace("${name}", event.getAuthor().getName());

                DungeonFighter monster = new DungeonFighter(monsterName+" - "+monsterLogo, monsterRank, 0);
                DungeonFighter player = DungeonFighter.createFromUser(this.player, event.getCommandManager());
                Random roll = new Random();

                int wtd = roll.nextInt(3);
                String menu;
                if (wtd==0) {
                    int coins = roll.nextInt((int) monsterRank*11);
                    menu = createTreasureMenu(player, coins, new String[]{}, event.getCommandManager().getBot().items, "you won "+coins+" coins", new String[]{"Go To Next Room"});
                } else if (wtd==1) {
                    int itemAmt = roll.nextInt((int) Math.max(1, Math.floor(monsterRank/2)));
                    List<String> items = new LinkedList<>();
                    JSONArray treasures = (JSONArray) event.getCommandManager().getBot().data.getSection("treasure").get("dungeon");
                    for (int i = 0; i < itemAmt; i++) {
                        items.add(event.getCommandManager().getBot().getItemsByRarity(treasures, 2));
                    }
                    String[] TreasureItems = new String[items.size()];
                    for (int i = 0; i < items.size(); i++) {
                        event.getCommandManager().getBot().giveUserItem(event.getAuthor(), items.get(i), 1);
                        TreasureItems[i] = items.get(i);
                    }
                    menu = createTreasureMenu(player, 0, TreasureItems, event.getCommandManager().getBot().items, "you won "+TreasureItems.length+" items", new String[]{"Go To Next Room"});
                } else {
                    int coins = roll.nextInt((int) monsterRank*5);
                    int itemAmt = roll.nextInt((int) Math.max(1, Math.floor(monsterRank/4)));
                    List<String> items = new LinkedList<>();
                    JSONArray treasures = (JSONArray) event.getCommandManager().getBot().data.getSection("treasure").get("dungeon");
                    for (int i = 0; i < itemAmt; i++) {
                        items.add(event.getCommandManager().getBot().getItemsByRarity(treasures, 5));
                    }
                    String[] TreasureItems = new String[items.size()];
                    for (int i = 0; i < items.size(); i++) {
                        event.getCommandManager().getBot().giveUserItem(event.getAuthor(), items.get(i), 1);
                        TreasureItems[i] = items.get(i);
                    }
                    menu = createTreasureMenu(player, coins, TreasureItems, event.getCommandManager().getBot().items, "you won "+coins+" and "+TreasureItems.length+" items", new String[]{"Go To Next Room"});
                }
                showMenu = menu;
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                dungeonData.put("monster", new JSONArray());
                dungeonData.put("mode", 1);

                nextPatterns.add("1");
            }
        }
        event.getCommandManager().getBot().users.writeContent();
    }

    public String getSays(String name, String says) {
        return "you come across a " + name + " it looks at you, \"" + says + "\" it says,\nuh oh, i think we have to FIGHT";
    }

    public String createFightMenu(DungeonFighter monster, DungeonFighter player, String message, String[] options) {
        StringBuilder str = new StringBuilder("```md\n" +
                "> -----FIGHT-----\n" +
                "< MONSTER NAME: >/* " + monster.getName() + " *\n" +
                "< MONSTER RANK: >/* " + monster.getRank() + " *\n\n" +
                "# VS\n\n" +
                "< YOUR NAME: >/* " + player.getName() + " *\n" +
                "< YOUR RANK: >/* " + player.getRank() + " *\n" +
                "< YOUR MAGIC: >/* " + player.getMagic() + " *\n\n" +
                "OPTIONS\n" +
                "=======\n");
        for (int i = 0; i < options.length; i++) {
            str.append("[").append(i+1).append("]: ").append(options[i].toUpperCase()).append('\n');
        }
        str.append("[").append(options.length+1).append("]: ").append("EXIT").append('\n');
        str.append("\n").append(message).append("\n\n\n").append("```");
        return str.toString();
    }

    private String createTreasureMenu(DungeonFighter player, int coins, String[] items, DataBase itemData, String message, String[] options) {
        StringBuilder str = new StringBuilder("```md\n" +
                "> -----TREASURE-----\n" +
                "< COINS WON: >/* " + coins + " *\n" +
                "< ITEMS WON: >/* -");
        for (String item : items) {
            str.append(itemData.getSection(item).get("name"))
                    .append(" ")
                    .append(itemData.getSection(item).get("icon"))
                    .append("-");
        }
        str.append(" *\n\n" +
                "< YOUR NAME: >/* " + player.getName() + " *\n" +
                "< YOUR RANK: >/* " + player.getRank() + " *\n" +
                "< YOUR MAGIC: >/* " + player.getMagic() + " *\n\n" +
                "OPTIONS\n" +
                "=======\n");
        for (int i = 0; i < options.length; i++) {
            str.append("[").append(i+1).append("]: ").append(options[i].toUpperCase()).append('\n');
        }
        str.append("[").append(options.length+1).append("]: ").append("EXIT").append('\n');
        str.append("\n").append(message).append("\n\n\n").append("```");
        return str.toString();
    }

    public static String getFullGameNameNewLine() {
        return getFullGameName() + "\n";
    }

    public static String getFullGameName() {
        return getGameEmoji() + " " + getGameName() + " " + getGameEmoji();
    }

    public static String getGameName() {
        return gameName;
    }

    public static String getGameEmoji() {
        return gameEmoji;
    }
}
