package com.runningmanstudios.dankgamer.game.gambling.poker;

import com.runningmanstudios.dankgamer.game.gambling.cards.Card;
import com.runningmanstudios.dankgamer.game.gambling.cards.Deck;
import com.runningmanstudios.dankgamer.game.gambling.cards.Hand;
import com.runningmanstudios.discordlib.DiscordBot;
import com.runningmanstudios.discordlib.command.AttractListener;
import com.runningmanstudios.discordlib.command.AttractorFactory;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class PokerGame implements AttractListener {
    private boolean running = false;
    private User host;
    private Message lastShown = null;
    private int maxPlayers = 5;
    final int id;
    final Map<User, TextChannel> players = new LinkedHashMap<>();

    boolean inLobby = false;

    public PokerGame(User host, int id) {
        this.host = host;
        this.id = id;
    }

    public void onPlayerResponse(BotMessageEvent event) {
        System.out.println(event.getMessage().getContentRaw());
        if (inLobby) {
            sendLobbyTo(event.getBot(), event.getAuthor());
            if (event.getAuthor().getId().equals(host.getId()) && event.getMessage().getContentRaw().equalsIgnoreCase("start")) {
                if (players.size() < 2) {
                    event.reply("You must have more players!").queue();
                } else {
                    event.reply("Starting the game...").queue();
                    startLobby();
                }
            } else {

            }
        } else {
            EmbedBuilder menu = new EmbedBuilder();
            menu.setTitle("Poker")
                    .setDescription("Gambling Addiction is a serious problem, if you or anyone you know has a problem find help here: https://www.addictioncenter.com/drugs/gambling-addiction/. Here is a test poker menu")
                    .setFooter(event.getAuthor().getAsTag());

            Deck deck = Deck.createStandardDeck().shuffle();
            Hand hand = new Hand(deck, 5).collectAll();

            for (Card card : hand.getCards()) {
                menu.addField(card.toEmojiString(), "This card is a " + card.toCardString(), true);
            }

            menu.addField("Rank", hand.getScore().name(), false);

            event.getChannel().sendMessage(menu.build()).queue();
        }
    }

    public boolean isRunning() {
        return running;
    }

    public void startLobby() {
        if (inLobby) inLobby = false;
        for (User player : this.players.keySet()) {
            this.players.get(player).sendMessage(player.getAsMention() + ", Poker game #"+id+" has started").queue();
        }
    }

    public void stopLobby() {
        if (inLobby) inLobby = false;
        for (User player : this.players.keySet()) {
            this.players.get(player).sendMessage(player.getAsMention() + ", Poker game #"+id+" has ended").queue();
        }
    }

    public void start() {
        if (!running) running = true;
        inLobby = true;
    }

    public void stop() {
        if (running) running = false;
    }

    public Map<User, TextChannel> getPlayers() {
        return players;
    }

    public Message getLastShown() {
        return lastShown;
    }

    public int getId() {
        return id;
    }

    public boolean requestJoin(DiscordBot bot, User user, TextChannel channel) {
        if (this.players.size() == maxPlayers) return false;
        this.players.put(user, channel);

        sendLobbyTo(bot, user);

        return true;
    }

    public boolean requestLeave(User user) {
        this.players.remove(user);
        if (host.getId().equals(user.getId()) && !this.players.isEmpty()) {
            this.host = this.players.keySet().toArray(new User[0])[0];
        }
        for (User player : this.players.keySet())
            this.players.get(player).sendMessage(player.getAsMention() + ", User " + user.getAsMention() + " left the poker game.").queue();
        return true;
    }

    public void sendLobbyTo(DiscordBot bot, User user) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(host.getAsTag() + "'s Poker Game")
                .setDescription(createLobbyScreen());
        if (host.getId().equals(user.getId())) {
            embed.setFooter(this.players.size() + "/" + maxPlayers + " players | Type \""+bot.getPrefix()+"poker start\" to start the game");
            bot.commandManager.setAttractor(user, AttractorFactory.createAnyAttractor(this));
        }
        else
            embed.setFooter(this.players.size() + "/" + maxPlayers + " players");

        this.players.get(user).sendMessage(embed.build()).queue();
    }

    public boolean hasUser(User user) {
        for (User player : this.players.keySet())
            if (player.getId().equals(user.getId()))
                return true;
        return false;
    }

    public User getHost() {
        return host;
    }

    public String createLobbyScreen() {
        return """
                :green_square::green_square::green_square::green_square::green_square::green_square::green_square::green_square:
                :green_square::hearts::green_square::green_square::green_square::green_square::spades::green_square:
                :green_square::green_square::green_square::green_square::green_square::green_square::green_square::green_square:
                :green_square: :regional_indicator_p: :regional_indicator_o: :regional_indicator_k: :regional_indicator_e: :regional_indicator_r: :green_square:
                :green_square::green_square::green_square::flower_playing_cards::flower_playing_cards::green_square::green_square::green_square:
                :green_square::green_square::green_square::green_square::green_square::green_square::green_square::green_square:
                :green_square::clubs::green_square::green_square::green_square::green_square::diamonds::green_square:
                :green_square::green_square::green_square::green_square::green_square::green_square::green_square::green_square:""";
    }

    @Override
    public void onAttract(BotMessageEvent event) {
        onPlayerResponse(event);
    }
}
