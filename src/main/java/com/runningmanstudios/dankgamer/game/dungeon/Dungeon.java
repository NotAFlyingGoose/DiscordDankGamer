package com.runningmanstudios.dankgamer.game.dungeon;

import com.runningmanstudios.dankgamer.game.GameInstance;
import com.runningmanstudios.discordlib.Bot;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.data.MemberData;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class Dungeon extends GameInstance {
    private final static String gameName = "Dungeon";
    private final static String gameEmoji = ":european_castle:";
    private static final int MODE_TO_RESET = 100;

    public Dungeon(Guild guild, User player, BotMessageEvent event) {
        super(guild, player);

        nextPatterns.add("continue");
        start();
        MemberData userData = event.getMemberData();

        if (userData.game_dungeon_mode == MODE_TO_RESET || userData.game_dungeon_monster_id == null) {
            userData = userData.withDungeon(-2, 1, 2, "", 0);
            DataBase.updateMemberData(userData);
        } else {
            userData = userData.withDungeon(1, userData.game_dungeon_rank, userData.game_dungeon_magic, userData.game_dungeon_monster_id, userData.game_dungeon_monster_rank);
            DataBase.updateMemberData(userData);

            String menu = """
            ```md
            # type `continue` to continue your game...
            ```
            """;
            event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);

            return;
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

    public void onResponse(BotMessageEvent event) {
        nextPatterns.clear();
        MemberData userData = event.getMemberData();
        JSONObject monsters = (JSONObject) event.getCommandManager().getBot().data.get("monsters");
        int mode = userData.game_dungeon_mode;
        int rank = userData.game_dungeon_rank;
        float magic = userData.game_dungeon_magic;
        String monster_id = userData.game_dungeon_monster_id;
        float monster_rank = userData.game_dungeon_monster_rank;
        switch (mode) {
            case -2 -> {
                String menu = createFightMenu(new DungeonFighter("Skella-Bone - 🦴", 1, 0), DungeonFighter.createFromUser(userData, player), getSays("Skella-Bone", "*rattle*"), new String[]{"next"});
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                mode++;
                nextPatterns.add("1");
            }
            case -1 -> {
                String menu = createFightMenu(new DungeonFighter("Skella-Bone - 🦴", 1, 0), DungeonFighter.createFromUser(userData, player), "The monster has the same level as you, so you can not fight him normally. you have to use your magic\nyou can use magic instead of rank to kill a monster, but afterwards you lose some magic", new String[]{"fight"});
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                mode++;
                nextPatterns.add("1");
            }
            case 0 -> {
                rank++;
                magic--;
                String menu = createFightMenu(new DungeonFighter("Skella-Bone - 🦴", 1, 0), DungeonFighter.createFromUser(userData, player), "you kill the Skella-Bone\nyou killed the monster but lost some magic, because you beat this monster, you up your rank,\nsometimes, you can't beat a monster, with or without magic, so just go away for a bit and you will gain magic from just chatting\n", new String[]{"continue through dungeon"});
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                mode++;
                nextPatterns.add("1");
            }
            case 1 -> {
                DungeonFighter player = DungeonFighter.createFromUser(userData, this.player);

                boolean resetRank = false;
                if (monster_id.isEmpty()) {
                    resetRank = true;
                    int monstersLength = monsters.size();
                    double monsterIndex = (Math.floor(Math.random() * monstersLength));
                    int i = 0;
                    for (Object monster : monsters.keySet()) {
                        if (i == monsterIndex) {
                            monster_id = monster.toString();
                            break;
                        }
                        i++;
                    }
                }

                JSONObject monsterData = (JSONObject)monsters.get(monster_id);
                String monsterLogo = monsterData.get("logo").toString();
                String monsterSays = monsterData.get("says").toString()
                        .replace("${said}", event.getMessage().getContentRaw())
                        .replace("${name}", event.getAuthor().getName());
                float monsterBase = ((Number)monsterData.get("base")).floatValue();
                if (resetRank) {
                    int min = (int) (player.getRank()-2);
                    int max = (int) (player.getRank()+7);
                    monster_rank = Math.max(1, Math.max(min, monsterBase + (float) (new Random().nextInt(max - min) + min)));
                }
                DungeonFighter monster = new DungeonFighter(monster_id+" - "+monsterLogo, monster_rank, 0);

                String menu = createFightMenu(monster, player, getSays(monster_id, monsterSays), new String[]{"fight", "use magic"});

                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);

                mode++;

                nextPatterns.add("1");
                nextPatterns.add("2");
            }
            case 2 -> {
                JSONObject monsterData = (JSONObject)monsters.get(monster_id);
                String monsterLogo = monsterData.get("logo").toString();

                DungeonFighter monster = new DungeonFighter(monster_id+" - "+monsterLogo, monster_rank, 0);
                DungeonFighter player = DungeonFighter.createFromUser(userData, this.player);

                int option = Integer.parseInt(event.getMessage().getContentRaw());
                if (option==1) {
                    if (monster_rank<player.getRank()) {
                        String menu = createFightMenu(monster, player, "you killed the " + monster_id, new String[]{"claim treasure"});
                        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                        rank++;
                        magic+=0.2;
                        mode++;

                        nextPatterns.add("1");
                    }
                    else {
                        String menu = createFightMenu(monster, player, "The monsters is too good for your filthy \"rank\"", new String[]{"fight", "use magic"});
                        if (monsterData.get("onRank")!=null) menu = createFightMenu(monster, player, "The monsters is too good for your filthy \"rank\". It says, \""+monsterData.get("onRank").toString().replace("${said}", event.getMessage().getContentRaw()).replace("${name}", event.getAuthor().getName())+"\"", new String[]{"fight", "use magic"});
                        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);

                        nextPatterns.add("1");
                        nextPatterns.add("2");
                    }
                } else if (option==2) {
                    if (monster_rank<player.getMagic()) {
                        String menu = createFightMenu(monster, player, "you killed the " + monster_id, new String[]{"claim treasure"});
                        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                        rank++;
                        magic-=monster.getRank()/2;
                        mode++;

                        nextPatterns.add("1");
                    }
                    else {
                        String menu = createFightMenu(monster, player, "The monster is stronger than your weak magic", new String[]{"fight", "use magic"});
                        if (monsterData.get("onMagic")!=null) menu = createFightMenu(monster, player, "The monster is stronger than your magic. It says, \""+monsterData.get("onMagic").toString().replace("${said}", event.getMessage().getContentRaw()).replace("${name}", event.getAuthor().getName())+"\"", new String[]{"fight", "use magic"});
                        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);

                        nextPatterns.add("1");
                        nextPatterns.add("2");
                    }
                }
            }
            case 3 -> {
                DungeonFighter player = DungeonFighter.createFromUser(userData, this.player);
                Random roll = new Random();

                int wtd = roll.nextInt(3);
                String menu;
                if (wtd==0) {
                    int coins = roll.nextInt((int) ((monster_rank+1) * 11));
                    menu = createTreasureMenu(player, coins, new String[]{}, event.getCommandManager().getBot(), "you won "+coins+" coins", new String[]{"Go To Next Room"});
                } else if (wtd==1) {
                    int itemAmt = roll.nextInt((int) Math.max(1, Math.floor(monster_rank/2)));
                    List<String> items = new LinkedList<>();
                    JSONArray treasures = (JSONArray) ((JSONObject) event.getCommandManager().getBot().data.get("treasure")).get("dungeon");
                    for (int i = 0; i < itemAmt; i++) {
                        items.add(event.getCommandManager().getBot().getItemsByRarity(treasures, 2));
                    }
                    String[] TreasureItems = new String[items.size()];
                    for (int i = 0; i < items.size(); i++) {
                        event.getCommandManager().getBot().giveUserItem(DataBase.getMemberData(event.getGuild().getId(), event.getAuthor().getId()), items.get(i), 1);
                        TreasureItems[i] = items.get(i);
                    }
                    menu = createTreasureMenu(player, 0, TreasureItems, event.getCommandManager().getBot(), "you won "+TreasureItems.length+" items", new String[]{"Go To Next Room"});
                } else {
                    int coins = roll.nextInt((int) ((monster_rank+1) * 5));
                    int itemAmt = roll.nextInt((int) Math.max(2, Math.floor(monster_rank/4)));
                    List<String> items = new LinkedList<>();
                    JSONArray treasures = (JSONArray) ((JSONObject) event.getCommandManager().getBot().data.get("treasure")).get("dungeon");
                    for (int i = 0; i < itemAmt; i++) {
                        items.add(event.getCommandManager().getBot().getItemsByRarity(treasures, 5));
                    }
                    System.out.println(coins);
                    System.out.println(itemAmt);
                    System.out.println(items);
                    String[] TreasureItems = new String[items.size()];
                    for (int i = 0; i < items.size(); i++) {
                        event.getCommandManager().getBot().giveUserItem(DataBase.getMemberData(event.getGuild().getId(), event.getAuthor().getId()), items.get(i), 1);
                        TreasureItems[i] = items.get(i);
                    }
                    menu = createTreasureMenu(player, coins, TreasureItems, event.getCommandManager().getBot(), "you won "+coins+" coins and "+TreasureItems.length+" items", new String[]{"Go To Next Room"});
                }
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
                monster_id = "";
                monster_rank = 0;
                mode = 1;

                nextPatterns.add("1");
            }
        }

        userData = userData.withDungeon(mode, rank, magic, monster_id, monster_rank);
        DataBase.updateMemberData(userData);
    }

    @Override
    public void removePlayerData(Bot bot) {
        DataBase.updateMemberData(DataBase.getMemberData(guild.getId(), player.getId()).withDungeon(MODE_TO_RESET, 0, 0, "", 0));
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

    private String createTreasureMenu(DungeonFighter player, int coins, String[] items, Bot bot, String message, String[] options) {
        StringBuilder str = new StringBuilder("```md\n" +
                "> -----TREASURE-----\n" +
                "< COINS WON: >/* " + coins + " *\n" +
                "< ITEMS WON: >/* -");
        for (String item : items) {
            str.append(bot.getItem(item).getName())
                    .append(" ")
                    .append(bot.getItem(item).getIcon())
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
