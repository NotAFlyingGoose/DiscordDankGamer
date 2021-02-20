package com.runningmanstudios.dankgamer.game.oregontrail;

import com.runningmanstudios.dankgamer.game.GameInstance;
import com.runningmanstudios.discordlib.DiscordBot;
import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.util.Objects;

public class OregonTrailGame extends GameInstance {
    private final static String MOUNTAIN = ":mountain:";
    private final static String MOUNTAINSNOW = ":mountain_snow:";
    private final static String NOTHING = ":blue_square:";
    private final static String GRASS = ":green_square:";
    private final static String SNOW = ":white_square:";

    private final static String gameName = "The Oregon Trail";
    private final static String gameEmoji = ":cow2::tent:";
    private final static String dataName = "oregon_trail";

    public OregonTrailGame(Guild guild, User player, BotMessageEvent event) {
        super(guild, player);

        nextPatterns.add("continue");
        start();

        /*if (!userData.containsKey(dataName)) { // first time setup
            JSONObject game = new JSONObject();
            game.put("rod", "fishing_rod_basic");
            game.put("location", "fishing_locations_uncle");
            game.put("mode", 0);
            userData.put(dataName, game);

            event.getBot().users.writeContent();
        } else {
            JSONObject previousGame = (JSONObject) userData.get(dataName);
            if (((Number) previousGame.get("mode")).intValue() >= 100 || ((Number) previousGame.get("mode")).intValue() < -2) { // restart setup.
                previousGame.put("rod", "fishing_rod_basic");
                previousGame.put("location", "fishing_locations_uncle");
                previousGame.put("mode", 0);
                userData.put("fishing", previousGame);

                event.getBot().users.writeContent();
            } else { // continue game setup
                previousGame.put("mode", 0);
                userData.put("fishing", previousGame);

                event.getBot().users.writeContent();
                String menu = """
                        ```md
                        # type `continue` to continue your game...
                        ```
                        """;
                event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);

                event.getBot().users.writeContent();
                return;
            }
        }*/

        String menu = """
                ```md
                < This is just an oregon trail ripoff, there is no backstory. >
                
                
                # type `continue` to continue story...
                ```
                """;
        event.getChannel().sendMessage(getFullGameNameNewLine() + menu).queue(message -> lastShown = message);
    }

    @Override
    public void onResponse(BotMessageEvent event) {
        nextPatterns.clear();

        event.getChannel().sendMessage(Util.createSimpleEmbed(getFullGameName(), getWorld(GRASS, MOUNTAIN, 5), event.getAuthor().getAsTag())).queue();

    }

    @Override
    public void removePlayerData(DiscordBot bot) {

    }

    public String getWorld(String foreground, String background, int playerX) {
        StringBuilder send = new StringBuilder();

        String[][] screen = new String[4][8];

        for (int y = 0; y < screen.length; y++) {
            for (int x = 0; x < screen[y].length; x++) {
                if (y == 0) {
                    screen[y][x] = background;
                    continue;
                }
                if (y == screen.length - 1) {
                    screen[y][x] = foreground;
                    continue;
                }
                if (y == screen.length - 2 && x == playerX) {
                    screen[y][x] = ":tent:";
                    continue;
                }
                if (y == screen.length - 2 && x == playerX - 1) {
                    screen[y][x] = ":cow2:";
                    continue;
                }

                screen[y][x] = NOTHING;
            }

            for (String cell : screen[y])
                send.append(cell);
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
