package com.runningmanstudios.dankgamer.game.gambling;

import com.runningmanstudios.dankgamer.game.GameInstance;
import com.runningmanstudios.discordlib.Bot;
import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.event.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GamblingGame extends GameInstance {
    private static final int WAITING = 0;
    private static final int CATCHING = 1;
    private static final int BADTHROW = 2;
    private static final int CAUGHT = 3;

    private final static String gameName = "Fishing";
    private final static String gameEmoji = ":fishing_pole_and_fish:";

    private Timer fishingTimer = new Timer();

    private Random r = new Random();
    private int rodX = 3, rodY = -1;

    public GamblingGame(User player, CommandEvent event) {
        super(player);

        nextPatterns.add("continue");
        start();
        JSONObject userData = event.getCommandManager().getBot().getUserData(event.getAuthor());
        if (!event.getCommandManager().getBot().doesUserHaveItem(player, "fishing_rod_basic"))
            event.getCommandManager().getBot().giveUserItem(player, "fishing_rod_basic", 1);

        if (!userData.containsKey("fishing")) { // first time setup
            JSONObject game = new JSONObject();
            game.put("rod", "fishing_rod_basic");
            game.put("location", "fishing_locations_uncle");
            game.put("mode", 0);
            userData.put("fishing", game);

            event.getCommandManager().getBot().users.writeContent();
        } else {
            JSONObject previousGame = (JSONObject) userData.get("fishing");
            if (((Number) previousGame.get("mode")).intValue() >= 100 || ((Number) previousGame.get("mode")).intValue() < -2) { // restart setup.
                previousGame.put("rod", "fishing_rod_basic");
                previousGame.put("location", "fishing_locations_uncle");
                previousGame.put("mode", 0);
                userData.put("fishing", previousGame);

                event.getCommandManager().getBot().users.writeContent();
            } else { // continue game setup
                previousGame.put("mode", 0);
                userData.put("fishing", previousGame);

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
    public void onResponse(CommandEvent event) {
        nextPatterns.clear();
        JSONObject userData = event.getAuthorData();
        JSONObject fishingData = (JSONObject) userData.get("fishing");
        int mode = ((Number) fishingData.get("mode")).intValue();
        String fishingRod = (String) fishingData.get("rod");
        float rodLootMultiplier = ((Number) event.getCommandManager().getBot().items.getSection(fishingRod).get("luck")).floatValue();
        float rodSpeed = ((Number) event.getCommandManager().getBot().items.getSection(fishingRod).get("speed")).floatValue();

        JSONObject location = (JSONObject) ((JSONObject) event.getCommandManager().getBot().data.getSection("locations").get("fishing")).get(fishingData.get("location"));
        float locationMultiplier = ((Number) location.get("luck")).floatValue();

        switch (mode) {
            case -2 -> {
                if (((JSONObject) event.getCommandManager().getBot().data.getSection("locations").get("fishing")).get(event.getMessage().getContentRaw()) != null) {
                    fishingData.put("location", event.getMessage().getContentRaw());
                    JSONObject locations = (JSONObject) event.getCommandManager().getBot().data.getSection("locations").get("fishing");

                    event.reply("Location successfully set to " + ((JSONObject) locations.get(event.getMessage().getContentRaw())).get("name").toString() + " - " + ((JSONObject) locations.get(event.getMessage().getContentRaw())).get("icon").toString() + ". Type `back` to go to menu").queue(message -> lastShown = message);
                } else {
                    event.reply("That is not a valid location. Type `back` to go to menu").queue(message -> lastShown = message);
                }
                fishingData.put("mode", 0);
                nextPatterns.add("back");
            }
            case -1 -> {
                if (event.getCommandManager().getBot().items.getSection(event.getMessage().getContentRaw()) != null) {
                    if (event.getCommandManager().getBot().doesUserHaveItem(event.getAuthor(), event.getMessage().getContentRaw())) {
                        fishingData.put("rod", event.getMessage().getContentRaw());
                        event.reply("Rod successfully set to " + event.getCommandManager().getBot().items.getSection(event.getMessage().getContentRaw()).get("name") + " - \uD83C\uDFA3. Type `back` to go to menu").queue(message -> lastShown = message);
                    } else {
                        event.reply("You do not have that item in your inventory. Type `back` to go to menu").queue(message -> lastShown = message);
                    }
                } else {
                    event.reply("That is not a valid fishing rod. Type `back` to go to menu").queue(message -> lastShown = message);
                }
                fishingData.put("mode", 0);
                nextPatterns.add("back");
            }
            case 0 -> {
                if (event.getMessage().getContentRaw().equals("location")) {
                    EmbedBuilder menu = new EmbedBuilder();
                    menu.setTitle(getFullGameName());
                    menu.setDescription("Available Locations:");
                    menu.setFooter("Enter fishing rod id.");

                    JSONObject locations = (JSONObject) event.getCommandManager().getBot().data.getSection("locations").get("fishing");

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
                    fishingData.put("mode", -2);
                }
                if (event.getMessage().getContentRaw().equals("rod")) {
                    EmbedBuilder menu = new EmbedBuilder();
                    menu.setTitle(getFullGameName());
                    menu.setDescription("Available Rods:");
                    menu.setFooter("Enter fishing rod id.");

                    DataBase items = event.getCommandManager().getBot().items;
                    JSONObject inv = (JSONObject) event.getCommandManager().getBot().getUserData(event.getAuthor()).get("inv");

                    for (Object item : inv.keySet()) {
                        if (!item.toString().startsWith("fishing_rod")) continue;
                        menu.addField(
                                items.getSection(item.toString()).get("name").toString(),
                                "**- Icon:** " + items.getSection(item.toString()).get("icon").toString() + " \n" +
                                        "**- Rarity:** " + event.getCommandManager().getBot().ItemRarityString(items.getSection(item.toString()).get("rarity").toString()) + " \n" +
                                        "**- Luck:** " + items.getSection(item.toString()).get("luck").toString() + " \n" +
                                        "**- Speed Boost:** " + items.getSection(item.toString()).get("speed").toString() + " \n" +
                                        "**- Item id:** `" + item.toString() + "`", true);
                    }

                    event.getChannel().sendMessage(menu.build()).queue(message -> lastShown = message);
                    nextPatterns.add(".*");
                    fishingData.put("mode", -1);
                }
                if (event.getMessage().getContentRaw().equals("fish")) {
                    fishingData.put("mode", 1);
                    onResponse(event);
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
                    menu.addField("Fishing Rod", event.getCommandManager().getBot().items.getSection(fishingRod).get("name").toString(), true);

                    nextPatterns.add("location");
                    nextPatterns.add("rod");
                    nextPatterns.add("fish");
                    event.getChannel().sendMessage(menu.build()).queue(message -> lastShown = message);
                }
            }
            case 1 -> {
                event.getChannel().sendMessage(
                        Util.createSimpleEmbed(
                                getFullGameName(),
                                getWorld(rodX, rodY, GamblingGame.WAITING),
                                "Reactions will only start counting after half of the reactions have been done"))
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
                                        message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, GamblingGame.BADTHROW), "That throw was shit. Please type \"rethrow\" to cast your line again.")).queue(message -> lastShown = message);
                                    } else {

                                        float power = (inputPower * 0.1f);

                                        double throwY = Math.ceil(power * 7.0f);

                                        rodX = rodX + (r.nextInt(4) - 2);
                                        if (rodX < 1) rodX = 1;
                                        if (rodX > 7) rodX = 7;
                                        rodY = (int) throwY;
                                        message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, GamblingGame.WAITING), "Type \"rethrow\" to cast the rod again, or type \"accept\" to fish at this position.")).queue(message -> lastShown = message);
                                    }
                                    fishingData.put("mode", 2);
                                }
                            }, 5000);
                        });
                rodY = -1;
                nextPatterns.add("rethrow");
                nextPatterns.add("accept");
            }
            case 2 -> {
                nextPatterns.add("continue");
                if (rodY < 1) event.reply("You must throw your rod by playing the game!").queue();

                if (event.getMessage().getContentRaw().equals("rethrow") || rodY < 1) {
                    fishingData.put("mode", 1);
                    onResponse(event);
                }
                else if (event.getMessage().getContentRaw().equals("accept")) {
                    final boolean[] fishAvailable = {false};
                    event.getChannel().sendMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, GamblingGame.WAITING), "Wait until you see a fish, then react with the fishing rod. If you want to stop then react to the stop sign"))
                            .queue(msg -> msg.editMessage(msg).queue(message -> {
                                lastShown = message;
                                FishingMessageReactor fmr = new FishingMessageReactor(message) {
                                    @Override
                                    public void Done() {
                                        if (STOPPED) {
                                            fishingTimer.cancel();
                                            message.editMessage(Util.createSimpleEmbed(
                                                    getFullGameName(),
                                                    getWorld(rodX, -1, GamblingGame.WAITING),
                                                    "Cancelled. Type \"continue\" to go to the menu."))
                                                    .queue(message -> lastShown = message);
                                            fishingData.put("mode", 0);
                                        }
                                        if (FISHING && !fishAvailable[0]) {
                                            fishingTimer.cancel();
                                            message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, GamblingGame.BADTHROW), "You missed the fish. Type \"continue\" to go to the menu.")).queue(msg -> lastShown = message);
                                            fishingData.put("mode", 0);
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
                                                getWorld(rodX, rodY, GamblingGame.CATCHING),
                                                "Wait until you see a fish, then react with the fishing rod. If you want to stop then react to the stop sign"))
                                                .queue(message -> {
                                                    lastShown = message;
                                                    long available = 1000 * (r.nextInt(2 - 1) + 1);
                                                    try {
                                                        Thread.sleep(available);
                                                    } catch (InterruptedException e) {
                                                        e.printStackTrace();
                                                    }
                                                    if (fmr.FISHING) {
                                                        message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, GamblingGame.CAUGHT), "You caught a fish!. Type \"continue\" to go to collect your prize!")).queue(msg -> lastShown = message);
                                                        fishingData.put("mode", 3);

                                                    } else {
                                                        message.editMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(rodX, rodY, GamblingGame.BADTHROW), "You missed the fish. Type \"continue\" to go to the menu.")).queue(msg -> lastShown = message);
                                                        fishingData.put("mode", 0);
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
                JSONArray treasures = (JSONArray) event.getCommandManager().getBot().data.getSection("treasure").get("fishing");
                String item = (event.getCommandManager().getBot().getItemsByRarity(treasures, 3 - (rodLootMultiplier * locationMultiplier)));
                event.getCommandManager().getBot().giveUserItem(event.getAuthor(), item, 1);
                MessageEmbed menu = Util.createSimpleEmbed("Your Prize", "You found " + event.getCommandManager().getBot().items.getSection(item).get("name").toString() + " - " + event.getCommandManager().getBot().items.getSection(item).get("icon").toString() + ".", "Type \"continue\" to go back to the menu.");
                event.getChannel().sendMessage(menu).queue(message -> lastShown = message);
                nextPatterns.add("continue");
                fishingData.put("mode", 0);
            }
        }

        event.getCommandManager().getBot().users.writeContent();
    }

    @Override
    public void removePlayerData(Bot bot) {
        bot.getUserData(player).remove("dungeon");
        bot.users.writeContent();
    }

    public String getWorld(int rodX, int rodY, int type) {
        StringBuilder send = new StringBuilder();
        String[] topWorld = new String[8];
        String[][] screen = new String[8][8];
        for (int i = 0; i < topWorld.length; i++) {
            topWorld[i] = ":green_square:";
            if (i == rodX + 1) {
                if (type == GamblingGame.WAITING)
                    topWorld[i] = ":flushed:";
                if (type == GamblingGame.CATCHING)
                    topWorld[i] = ":hushed:";
                if (type == GamblingGame.CAUGHT)
                    topWorld[i] = ":weary:";
                if (type == GamblingGame.BADTHROW)
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
                    if (type == GamblingGame.CATCHING || type == GamblingGame.CAUGHT)
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
