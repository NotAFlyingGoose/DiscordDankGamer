package com.runningmanstudios.dankgamer;

import com.runningmanstudios.dankgamer.game.fishing.FishCommand;
import com.runningmanstudios.dankgamer.game.gambling.blackjack.BJCommand;
import com.runningmanstudios.dankgamer.game.gambling.poker.PokerCommand;
import com.runningmanstudios.dankgamer.game.gambling.slot.SlotMachineCommand;
import com.runningmanstudios.dankgamer.game.oregontrail.OregonTrailCommand;
import com.runningmanstudios.dankgamer.standard.*;
import com.runningmanstudios.discordlib.Bot;
import com.runningmanstudios.dankgamer.game.dungeon.DungeonCommand;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.text.update.TextChannelUpdateNSFWEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.Random;

public class DankGamer extends ListenerAdapter {
    String[] meanies = new String[] {"retarded", "adopted", "stupid", "idiot", "sucks", "bitch", "fuck", "ass", "awful", "suck", "bot"};
    String[] meanResponses = new String[] {"no u...", ":sob:", "I just wanted to make you happy :cry:", "ur mean", "no u", "why are you so mean to me!", "that's not nice", "that's not a very christian thing to say my lad", "get your facts straight"};
    Random r = new Random();
    SelfUser self;
    Bot bot;

    public DankGamer() throws LoginException {
        bot = new Bot("/dankgamer/data/");
        bot.addCommand(new HelpCommand());
        bot.addCommand(new BankCommand());
        bot.addCommand(new LevelCommand());
        bot.addCommand(new AvatarCommand());
        bot.addCommand(new ShopCommand());
        bot.addCommand(new BuyCommand());
        bot.addCommand(new InvCommand());
        bot.addCommand(new DungeonCommand());
        bot.addCommand(new FishCommand());
        bot.addCommand(new PokerCommand());
        bot.addCommand(new BJCommand());
        bot.addCommand(new SlotMachineCommand());
        bot.addCommand(new OregonTrailCommand());

        bot.jda.addEventListener(this);

        self = bot.jda.getSelfUser();
    }

    public static void main(String[] args) throws LoginException {
        new DankGamer();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getChannelType() != ChannelType.PRIVATE || event.getAuthor().isBot()) return;

        boolean prefix = false;
        String messageContent = event.getMessage().getContentRaw();
        if (messageContent.substring(0, bot.getPrefix().length()).equalsIgnoreCase(bot.getPrefix())) prefix = true;

        messageContent = messageContent.substring(bot.getPrefix().length());

        if (event.getMessage().getContentRaw().equals("69"))
            event.getPrivateChannel().sendMessage("nice").queue();
        else if (prefix && messageContent.equalsIgnoreCase("porn"))
            event.getPrivateChannel().sendMessage("How about you look at this instead?\nhttps://i.imgur.com/Xm27PCH.jpg").queue();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        List<User> mentions = event.getMessage().getMentionedUsers();
        for (User m : mentions) {
            if (m.isBot() && m.getId().equals(self.getId())) {
                for (String mean : meanies)
                    if (event.getMessage().getContentRaw().contains(mean)) {
                        event.getChannel().sendMessage(meanResponses[r.nextInt(meanResponses.length - 1)]).queue();
                        return;
                    }
                break;
            }
        }
    }

    @Override
    public void onTextChannelUpdateNSFW(@NotNull TextChannelUpdateNSFWEvent event) {
        if (event.getChannel().isNSFW()) {
            event.getChannel().sendMessage("Lets gooooooo!").queue();
        }
    }

    @Override
    public void onTextChannelCreate(@NotNull TextChannelCreateEvent event) {
        if (event.getChannel().getName().equalsIgnoreCase("give-me-feet-pics")) {
            if (r.nextInt(25) == 1) {
                event.getChannel().sendMessage("ok fine, here are my feet pics http://shorturl.at/muyW9").queue();
            } else {
                event.getChannel().sendMessage("no.").queue();
            }
        }
        else if (event.getChannel().getName().equalsIgnoreCase("jesus-is-fire") || event.getChannel().getName().equalsIgnoreCase("read-the-bible")) {
            event.getChannel().sendMessage("yes").queue();
        }
    }
}
