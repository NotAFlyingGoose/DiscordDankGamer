package com.runningmanstudios.dankgamer.game.fishing;

import com.runningmanstudios.dankgamer.game.GameInstance;
import com.runningmanstudios.discordlib.DiscordBot;
import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.data.SQLDataBase;
import com.runningmanstudios.discordlib.data.Inventory;
import com.runningmanstudios.discordlib.data.Item;
import com.runningmanstudios.discordlib.data.MemberData;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FishingGame extends GameInstance {
    private static final int WAITING = 0;
    private static final int CATCHING = 1;
    private static final int BADTHROW = 2;
    private static final int CAUGHT = 3;

    private final static String gameName = "Fishing";
    private final static String gameEmoji = ":fishing_pole_and_fish:";
    private static final int MODE_TO_RESET = 100;

    private Timer fishingTimer = new Timer();

    private final Random r = new Random();
    private int rodX = 3, rodY = -1;

    public FishingGame(Guild guild, User player, BotMessageEvent event) {
        super(guild, player);

        nextPatterns.add("continue");
        start();
        MemberData userData = event.getMemberData();

        Inventory inv = new Inventory(event.getBot(), userData.inventory);
        if (!inv.hasItem("fishing_rod_basic"))
            event.getBot().giveUserItem(userData, "fishing_rod_basic", 1);

        if (userData.game_fishing_mode == MODE_TO_RESET || userData.game_fishing_rod == null) { // restart setup.
            userData = userData.withFishing(0, "fishing_rod_basic", "fishing_locations_uncle");
            SQLDataBase.updateMemberData(userData);
        } else { // continue game setup
            userData = userData.withFishing(0, userData.game_fishing_rod, userData.game_fishing_location);
            SQLDataBase.updateMemberData(userData);
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
                < After your Great Uncle's funeral all you are left with is a small part of his fortune and one of his smaller properties. >
                < It is a small location, with a cabin and a pond. When you go into the cabin you find lot's of dust and fishing supplies. >
                < You live in the city so you aren't really good at fishing but you decide to try it. >
                < After all, who doesn't love to fish? >
                
                
                # type `continue` to continue story...
                ```
                """;
        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
    }

    @Override
    public void onResponse(BotMessageEvent event) {
        nextPatterns.clear();
        final MemberData[] userData = {event.getMemberData()};
        AtomicInteger mode = new AtomicInteger(userData[0].game_fishing_mode);
        String fishingRod = userData[0].game_fishing_rod;
        String fishingLocation = userData[0].game_fishing_location;
        float rodLootMultiplier = ((Number) ((JSONObject) event.getBot().items.get(fishingRod)).get("luck")).floatValue();
        float rodSpeed = ((Number) ((JSONObject) event.getBot().items.get(fishingRod)).get("speed")).floatValue();

        JSONObject location = (JSONObject) ((JSONObject) ((JSONObject)event.getBot().data.get("locations")).get("fishing")).get(fishingLocation);
        float locationMultiplier = ((Number) location.get("luck")).floatValue();

        switch (mode.get()) {
            case -2 -> {
                if (((JSONObject) ((JSONObject) event.getBot().data.get("locations")).get("fishing")).get(event.getMessage().getContentRaw()) != null) {
                    fishingLocation = event.getMessage().getContentRaw();
                    location = (JSONObject) ((JSONObject) ((JSONObject)event.getBot().data.get("locations")).get("fishing")).get(fishingLocation);

                    event.reply("Location successfully set to " + location.get("name").toString() + " - " + location.get("icon").toString() + ". Type `back` to go to menu").queue(message -> lastShown = message);
                } else {
                    event.reply("That is not a valid location. Type `back` to go to menu").queue(message -> lastShown = message);
                }
                mode.set(0);
                nextPatterns.add("back");
            }
            case -1 -> {
                if (event.getBot().items.get(event.getMessage().getContentRaw()) != null) {
                    Inventory inv = new Inventory(event.getBot(), userData[0].inventory);
                    if (inv.hasItem(event.getMessage().getContentRaw())) {
                        fishingRod = event.getMessage().getContentRaw();
                        event.reply("Rod successfully set to " + event.getBot().getItem(fishingRod).getName() + " - \uD83C\uDFA3. Type `back` to go to menu").queue(message -> lastShown = message);
                    } else {
                        event.reply("You do not have that item in your inventory. Type `back` to go to menu").queue(message -> lastShown = message);
                    }
                } else {
                    event.reply("That is not a valid fishing rod. Type `back` to go to menu").queue(message -> lastShown = message);
                }
                mode.set(0);
                nextPatterns.add("back");
            }
            case 0 -> {
                if (event.getMessage().getContentRaw().equals("location")) {
                    EmbedBuilder menu = new EmbedBuilder();
                    menu.setTitle(getFullGameName());
                    menu.setDescription("Type the location id of the place you want to fish at.\nAvailable Locations:");

                    JSONObject locations = (JSONObject) ((JSONObject) event.getBot().data.get("locations")).get("fishing");

                    for (Object item : locations.keySet()) {
                        JSONObject itemLocation = (JSONObject) locations.get(item);
                        menu.addField(
                                itemLocation.get("name").toString(),
                                "**- Icon:** " + itemLocation.get("icon").toString() + " \n" +
                                        "**- Luck:** " + itemLocation.get("luck").toString() + " \n" +
                                        "**- Location id:** `" + item.toString() + "`", true);
                    }

                    event.getChannel().sendMessage(menu.build()).queue(message -> lastShown = message);
                    nextPatterns.add(".*");
                    mode.set(-2);
                }
                else if (event.getMessage().getContentRaw().equals("rod")) {
                    EmbedBuilder menu = new EmbedBuilder();
                    menu.setTitle(getFullGameName());
                    menu.setDescription("Type the item id of the rod you want to use.\nAvailable Rods:");

                    JSONObject items = event.getBot().items;
                    Inventory inv = new Inventory(event.getBot(), userData[0].inventory);

                    for (Item item : inv.getItems().keySet()) {
                        if (!item.getName().startsWith("fishing_rod")) continue;
                        menu.addField(
                                item.getName(),
                                "**- Icon:** " + item.getIcon() + " \n" +
                                        "**- Rarity:** " + item.getRarityString() + " \n" +
                                        "**- Luck:** " + ((JSONObject) items.get(item.toString())).get("luck").toString() + " \n" +
                                        "**- Speed Boost:** " + ((JSONObject) items.get(item.toString())).get("speed").toString() + " \n" +
                                        "**- Item id:** `" + item.getId() + "`", true);
                    }

                    event.getChannel().sendMessage(menu.build()).queue(message -> lastShown = message);
                    nextPatterns.add(".*");
                    mode.set(-1);
                }
                else if (event.getMessage().getContentRaw().equals("fish")) {
                    mode.set(1);
                    userData[0] = userData[0].withFishing(mode.get(), fishingRod, fishingLocation);
                    SQLDataBase.updateMemberData(userData[0]);
                    onResponse(event);
                    return;
                } else {
                    EmbedBuilder menu = new EmbedBuilder();
                    menu.setTitle(getFullGameName());
                    menu.setDescription("""         
                            Change Location 🚣 : `location`
                                                    
                            Change Fishing Rod 🎣 : `rod`
                                                    
                            Fish 🎣 : `fish`
                                                    
                            Exit 🛑 : `exit`
                            """);
                    menu.addField("Location", location.get("name").toString(), true);
                    menu.addField("Fishing Rod", ((JSONObject) event.getBot().items.get(fishingRod)).get("name").toString(), true);
                    menu.setFooter(event.getAuthor().getAsTag());

                    nextPatterns.add("location");
                    nextPatterns.add("rod");
                    nextPatterns.add("fish");
                    event.getChannel().sendMessage(menu.build()).queue(message -> lastShown = message);
                }
            }
            case 1 -> {
                String finalFishingRod = fishingRod;
                String finalFishingLocation = fishingLocation;
                event.getChannel().sendMessage(
                        Util.createSimpleEmbed(
                                getFullGameName(),
                                getWorld(rodX, rodY, FishingGame.WAITING),
                                event.getAuthor().getAsTag() + " | Reactions will only start counting after half of the reactions have been done"))
                        .queue(message -> {
                            lastShown = message;
                            PowerMessageReactor pmr = new PowerMessageReactor(message);

                            fishingTimer = new Timer();
                            fishingTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    event.getJDA().removeEventListener(pmr);

                                    int inputPower = pmr.countReacted();

                                    if (inputPower == 0) {
                                        message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, FishingGame.BADTHROW), event.getAuthor().getAsTag() + " | That throw was shit. Please type \"rethrow\" to cast your line again.")).queue(message -> lastShown = message);
                                    } else {

                                        float power = (inputPower * 0.1f);

                                        double throwY = Math.ceil(power * 7.0f);

                                        rodX = rodX + (r.nextInt(4) - 2);
                                        if (rodX < 1) rodX = 1;
                                        if (rodX > 7) rodX = 7;
                                        rodY = (int) throwY;
                                        message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, FishingGame.WAITING), event.getAuthor().getAsTag() + " | Type \"rethrow\" to cast the rod again, or type \"accept\" to fish at this position.")).queue(message -> lastShown = message);
                                    }
                                    mode.set(2);
                                    userData[0] = userData[0].withFishing(mode.get(), finalFishingRod, finalFishingLocation);
                                    SQLDataBase.updateMemberData(userData[0]);
                                }
                            }, 5000);
                        });
                rodY = -1;
                nextPatterns.add("rethrow");
                nextPatterns.add("accept");
            }
            case 2 -> {
                String finalFishingRod = fishingRod;
                String finalFishingLocation = fishingLocation;
                nextPatterns.add("continue");
                if (rodY < 1) event.reply("You must throw your rod by reacting to as much emojis as you can!").queue();

                if (event.getMessage().getContentRaw().equals("rethrow") || rodY < 1) {
                    mode.set(1);
                    userData[0] = userData[0].withFishing(mode.get(), fishingRod, fishingLocation);
                    SQLDataBase.updateMemberData(userData[0]);
                    onResponse(event);
                    return;
                }
                else if (event.getMessage().getContentRaw().equals("accept")) {
                    final boolean[] fishAvailable = {false};
                    event.getChannel().sendMessage(Util.createSimpleEmbed(getFullGameName(), "Calling Mr. Fish to ask if he'll play with you ...", event.getAuthor().getAsTag()))
                            .queue(msg -> msg.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, FishingGame.WAITING), event.getAuthor().getAsTag() + " | Wait until you see a fish, then react with the fishing rod. If you want to stop then react to the stop sign")).queue(message -> {
                                lastShown = message;
                                FishingMessageReactor fmr = new FishingMessageReactor(message) {
                                    @Override
                                    public void Done() {
                                        if (STOPPED && !fishAvailable[0]) {
                                            fishingTimer.cancel();
                                            message.editMessage(Util.createSimpleEmbed(
                                                    getFullGameName(),
                                                    getWorld(rodX, -1, FishingGame.WAITING),
                                                    event.getAuthor().getAsTag() + " | Cancelled. Type \"continue\" to go to the menu."))
                                                    .queue(message -> lastShown = message);
                                            mode.set(0);
                                            userData[0] = userData[0].withFishing(mode.get(), finalFishingRod, finalFishingLocation);
                                            SQLDataBase.updateMemberData(userData[0]);
                                        }
                                        if (FISHING && !fishAvailable[0]) {
                                            fishingTimer.cancel();
                                            message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, FishingGame.BADTHROW), event.getAuthor().getAsTag() + " | You missed the fish. Type \"continue\" to go to the menu.")).queue(msg -> lastShown = message);
                                            mode.set(0);
                                            userData[0] = userData[0].withFishing(mode.get(), finalFishingRod, finalFishingLocation);
                                            SQLDataBase.updateMemberData(userData[0]);
                                        }
                                    }
                                };

                                long sleepTime = (long) ((1000 * (r.nextInt(60 - 30) + 30)) * rodSpeed);

                                fishingTimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        fishAvailable[0] = true;
                                        message.editMessage(Util.createSimpleEmbed(
                                                getFullGameName(),
                                                getWorld(rodX, rodY, FishingGame.CATCHING),
                                                event.getAuthor().getAsTag() + " | Wait until you see a fish, then react with the fishing rod. If you want to stop then react to the stop sign"))
                                                .queue(message -> {
                                                    lastShown = message;
                                                    long available = 1000 * (r.nextInt(2 - 1) + 1);
                                                    try {
                                                        Thread.sleep(available);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (fmr.FISHING) {
                                                        message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, FishingGame.CAUGHT), event.getAuthor().getAsTag() + " | You caught a fish!. Type \"continue\" to go to collect your prize!")).queue(msg -> lastShown = message);
                                                        mode.set(3);
                                                        userData[0] = userData[0].withFishing(mode.get(), finalFishingRod, finalFishingLocation);
                                                        SQLDataBase.updateMemberData(userData[0]);

                                                    } else {
                                                        message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, FishingGame.BADTHROW), event.getAuthor().getAsTag() + " | You missed the fish. Type \"continue\" to go to the menu.")).queue(msg -> lastShown = message);
                                                        mode.set(0);
                                                        userData[0] = userData[0].withFishing(mode.get(), finalFishingRod, finalFishingLocation);
                                                        SQLDataBase.updateMemberData(userData[0]);
                                                    }
                                                });
                                    }
                                }, sleepTime);
                            }));
                } else if (event.getMessage().getContentRaw().equals("continue")) {
                    event.reply("You typed continue too early, please finish your game first").queue();
                }

            }
            case 3 -> {
                JSONArray treasures = (JSONArray) ((JSONObject) event.getBot().data.get("treasure")).get("fishing");
                String item = (event.getBot().getItemsByRarity(treasures, 3 - (rodLootMultiplier * locationMultiplier)));
                event.getBot().giveUserItem(userData[0], item, 1);
                Item itemObj = event.getBot().getItem(item);
                MessageEmbed menu = Util.createSimpleEmbed("Your Prize", "You found " + itemObj.getName() + " - " + itemObj.getIcon() + ".", event.getAuthor().getAsTag() + " | Type \"continue\" to go back to the menu.");
                event.getChannel().sendMessage(menu).queue(message -> lastShown = message);
                nextPatterns.add("continue");
                mode.set(0);
            }
        }
        userData[0] = userData[0].withFishing(mode.get(), fishingRod, fishingLocation);
        SQLDataBase.updateMemberData(userData[0]);
    }

    @Override
    public void removePlayerData(DiscordBot bot) {
        SQLDataBase.updateMemberData(SQLDataBase.getMemberData(guild.getId(), player.getId()).withFishing(MODE_TO_RESET, "", ""));
    }

    public String getWorld(int rodX, int rodY, int type) {
        StringBuilder send = new StringBuilder();
        String[] topWorld = new String[8];
        String[][] screen = new String[8][8];
        for (int i = 0; i < topWorld.length; i++) {
            topWorld[i] = ":green_square:";
            if (i == rodX + 1) {
                if (type == FishingGame.WAITING)
                    topWorld[i] = ":flushed:";
                if (type == FishingGame.CATCHING)
                    topWorld[i] = ":hushed:";
                if (type == FishingGame.CAUGHT)
                    topWorld[i] = ":weary:";
                if (type == FishingGame.BADTHROW)
                    topWorld[i] = ":dizzy_face:";
            }
            if (i == rodX) topWorld[i] = ":fishing_pole_and_fish:";

            send.append(topWorld[i]);
        }
        send.append('\n');

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                screen[y][x] = ":blue_square:";
                if (x == rodX && y < rodY) screen[y][x] = ":grey_exclamation:";
                if (x == rodX && y == rodY) {
                    if (type == FishingGame.CATCHING || type == FishingGame.CAUGHT)
                        screen[y][x] = ":fish:";
                    else
                        screen[y][x] = ":hook:";
                }

                send.append(screen[y][x]);
            }
            send.append('\n');
        }
        return send.toString();
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

    private class FishingMessageReactor extends ListenerAdapter {
        final String ogMessageId;

        boolean STOPPED = false;
        boolean FISHING = false;

        public FishingMessageReactor(Message message) {
            ogMessageId = message.getId();
            int[] done = {0};

            message.addReaction("\uD83D\uDED1").queue(m -> detect(done, 2, message.getJDA()));
            message.addReaction("\uD83C\uDFA3").queue(m -> detect(done, 2, message.getJDA()));
        }

        public void detect(int[] done, int max, JDA jda) {
            done[0]++;
            if (done[0] == max / 2) {
                jda.addEventListener(this);
            }
        }

        @Override
        public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
            if (!ogMessageId.equals(event.getMessageId()) ||
                    !Objects.requireNonNull(event.getUser()).getId().equals(player.getId())) return;

            switch (event.getReactionEmote().getEmoji()) {
                case "\uD83D\uDED1" -> STOPPED = true;
                case "\uD83C\uDFA3" -> FISHING = true;
            }

            if (STOPPED || FISHING) {
                Done();
            }
        }

        public void Done() {

        }
    }

    private class PowerMessageReactor extends ListenerAdapter {
        final String ogMessageId;

        boolean P = false;
        boolean O = false;
        boolean W = false;
        boolean E = false;
        boolean R = false;
        boolean ONE = false;
        boolean TWO = false;
        boolean THREE = false;
        boolean FOUR = false;
        boolean FIVE = false;

        public PowerMessageReactor(Message message) {
            ogMessageId = message.getId();
            int[] done = {0};

            message.addReaction("🇵").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("🇴").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("🇼").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("🇪").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("🇷").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("1️⃣").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("2️⃣").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("3️⃣").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("4️⃣").queue(m -> detect(done, 10, message.getJDA()));
            message.addReaction("5️⃣").queue(m -> detect(done, 10, message.getJDA()));
        }

        public void detect(int[] done, int max, JDA jda) {
            done[0]++;
            if (done[0] == max / 2) {
                jda.addEventListener(this);
            }
        }

        @Override
        public void onMessageReactionAdd(@Nonnull MessageReactionAddEvent event) {
            if (!ogMessageId.equals(event.getMessageId()) ||
                    !Objects.requireNonNull(event.getUser()).getId().equals(player.getId())) return;

            switch (event.getReactionEmote().getEmoji()) {
                case "🇵" -> P = true;
                case "🇴" -> O = true;
                case "🇼" -> W = true;
                case "🇪" -> E = true;
                case "🇷" -> R = true;
                case "1️⃣" -> ONE = true;
                case "2️⃣" -> TWO = true;
                case "3️⃣" -> THREE = true;
                case "4️⃣" -> FOUR = true;
                case "5️⃣" -> FIVE = true;
                default -> System.out.println(event.getReactionEmote().getEmoji());
            }
        }

        public boolean reactedAll() {
            return P && O && W && E && R && ONE && TWO && THREE && FOUR && FIVE;
        }

        public int countReacted() {
            int result = 0;
            if (P) result++;
            if (O) result++;
            if (W) result++;
            if (E) result++;
            if (R) result++;
            if (ONE) result++;
            if (TWO) result++;
            if (THREE) result++;
            if (FOUR) result++;
            if (FIVE) result++;

            return result;
        }
    }
}
