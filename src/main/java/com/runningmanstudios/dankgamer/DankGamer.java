package com.runningmanstudios.dankgamer;

import com.runningmanstudios.dankgamer.game.fishing.FishCommand;
import com.runningmanstudios.dankgamer.game.gambling.blackjack.BJCommand;
import com.runningmanstudios.dankgamer.game.gambling.poker.PokerCommand;
import com.runningmanstudios.dankgamer.game.gambling.slot.SlotMachineCommand;
import com.runningmanstudios.dankgamer.game.oregontrail.OregonTrailCommand;
import com.runningmanstudios.dankgamer.standard.*;
import com.runningmanstudios.discordlib.DiscordBot;
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
import java.time.Month;
import java.util.List;
import java.util.Random;

public class DankGamer extends ListenerAdapter {
    String[] swearWords = new String[] {"4r5e", "5h1t", "5hit", "a55", "anal", "anus", "ar5e", "arrse", "arse", "ass", "ass-fucker", "asses", "assfucker", "assfukka", "asshole", "assholes", "asswhole", "a_s_s", "b!tch", "b00bs", "b17ch", "b1tch", "ballbag", "balls", "ballsack", "bastard", "beastial", "beastiality", "bellend", "bestial", "bestiality", "bi+ch", "biatch", "bitch", "bitcher", "bitchers", "bitches", "bitchin", "bitching", "bloody", "blow job", "blowjob", "blowjobs", "boiolas", "bollock", "bollok", "boner", "boob", "boobs", "booobs", "boooobs", "booooobs", "booooooobs", "breasts", "buceta", "bugger", "bum", "bunny fucker", "butt", "butthole", "buttmuch", "buttplug", "c0ck", "c0cksucker", "carpet muncher", "cawk", "chink", "cipa", "cl1t", "clit", "clitoris", "clits", "cnut", "cock", "cock-sucker", "cockface", "cockhead", "cockmunch", "cockmuncher", "cocks", "cocksuck", "cocksucked", "cocksucker", "cocksucking", "cocksucks", "cocksuka", "cocksukka", "cok", "cokmuncher", "coksucka", "coon", "cox", "crap", "cum", "cummer", "cumming", "cums", "cumshot", "cunilingus", "cunillingus", "cunnilingus", "cunt", "cuntlick", "cuntlicker", "cuntlicking", "cunts", "cyalis", "cyberfuc", "cyberfuck", "cyberfucked", "cyberfucker", "cyberfuckers", "cyberfucking", "d1ck", "damn", "dick", "dickhead", "dildo", "dildos", "dink", "dinks", "dirsa", "dlck", "dog-fucker", "doggin", "dogging", "donkeyribber", "doosh", "duche", "dyke", "ejaculate", "ejaculated", "ejaculates", "ejaculating", "ejaculatings", "ejaculation", "ejakulate", "f u c k", "f u c k e r", "f4nny", "fag", "fagging", "faggitt", "faggot", "faggs", "fagot", "fagots", "fags", "fanny", "fannyflaps", "fannyfucker", "fanyy", "fatass", "fcuk", "fcuker", "fcuking", "feck", "fecker", "felching", "fellate", "fellatio", "fingerfuck", "fingerfucked", "fingerfucker", "fingerfuckers", "fingerfucking", "fingerfucks", "fistfuck", "fistfucked", "fistfucker", "fistfuckers", "fistfucking", "fistfuckings", "fistfucks", "flange", "fook", "fooker", "fuck", "fucka", "fucked", "fucker", "fuckers", "fuckhead", "fuckheads", "fuckin", "fucking", "fuckings", "fuckingshitmotherfucker", "fuckme", "fucks", "fuckwhit", "fuckwit", "fudge packer", "fudgepacker", "fuk", "fuker", "fukker", "fukkin", "fuks", "fukwhit", "fukwit", "fux", "fux0r", "f_u_c_k", "gangbang", "gangbanged", "gangbangs", "gaylord", "gaysex", "goatse", "God", "god-dam", "god-damned", "goddamn", "goddamned", "hardcoresex", "hell", "heshe", "hoar", "hoare", "hoer", "homo", "hore", "horniest", "horny", "hotsex", "jack-off", "jackoff", "jap", "jerk-off", "jism", "jiz", "jizm", "jizz", "kawk", "knob", "knobead", "knobed", "knobend", "knobhead", "knobjocky", "knobjokey", "kock", "kondum", "kondums", "kum", "kummer", "kumming", "kums", "kunilingus", "l3i+ch", "l3itch", "labia", "lust", "lusting", "m0f0", "m0fo", "m45terbate", "ma5terb8", "ma5terbate", "masochist", "master-bate", "masterb8", "masterbat*", "masterbat3", "masterbate", "masterbation", "masterbations", "masturbate", "mo-fo", "mof0", "mofo", "mothafuck", "mothafucka", "mothafuckas", "mothafuckaz", "mothafucked", "mothafucker", "mothafuckers", "mothafuckin", "mothafucking", "mothafuckings", "mothafucks", "mother fucker", "motherfuck", "motherfucked", "motherfucker", "motherfuckers", "motherfuckin", "motherfucking", "motherfuckings", "motherfuckka", "motherfucks", "muff", "mutha", "muthafecker", "muthafuckker", "muther", "mutherfucker", "n1gga", "n1gger", "nazi", "nigg3r", "nigg4h", "nigga", "niggah", "niggas", "niggaz", "nigger", "niggers", "nob", "nob jokey", "nobhead", "nobjocky", "nobjokey", "numbnuts", "nutsack", "orgasim", "orgasims", "orgasm", "orgasms", "p0rn", "pawn", "pecker", "penis", "penisfucker", "phonesex", "phuck", "phuk", "phuked", "phuking", "phukked", "phukking", "phuks", "phuq", "pigfucker", "pimpis", "piss", "pissed", "pisser", "pissers", "pisses", "pissflaps", "pissin", "pissing", "pissoff", "poop", "porn", "porno", "pornography", "pornos", "prick", "pricks", "pron", "pube", "pusse", "pussi", "pussies", "pussy", "pussys", "rectum", "retard", "rimjaw", "rimming", "s hit", "s.o.b.", "sadist", "schlong", "screwing", "scroat", "scrote", "scrotum", "semen", "sex", "sh!+", "sh!t", "sh1t", "shag", "shagger", "shaggin", "shagging", "shemale", "shi+", "shit", "shitdick", "shite", "shited", "shitey", "shitfuck", "shitfull", "shithead", "shiting", "shitings", "shits", "shitted", "shitter", "shitters", "shitting", "shittings", "shitty", "skank", "slut", "sluts", "smegma", "smut", "snatch", "son-of-a-bitch", "spac", "spunk", "s_h_i_t", "t1tt1e5", "t1tties", "teets", "teez", "testical", "testicle", "tit", "titfuck", "tits", "titt", "tittie5", "tittiefucker", "titties", "tittyfuck", "tittywank", "titwank", "tosser", "turd", "tw4t", "twat", "twathead", "twatty", "twunt", "twunter", "v14gra", "v1gra", "vagina", "viagra", "vulva", "w00se", "wang", "wank", "wanker", "wanky", "whoar", "whore", "willies", "willy", "xrated", "xxx"};
    String[] responses = new String[] {"no u...", ":sob:", "I just wanted to make you happy :cry:", "ur mean", "no u", "why are you so mean to me!", "that's not nice", "that's not a very christian thing to say my lad", "get your facts straight", "please stop bullying me", "stop", "stop please", "please stop", "stop :sob: :sob: :sob:", "stop please :sob:", "please stop :sob:", ":crying:", ":crying_cat_face:"};
    Random r = new Random();
    SelfUser self;
    DiscordBot bot;

    public DankGamer() {
        bot = new DiscordBot("/dankgamer/data/");
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
        String command = event.getMessage().getContentRaw();
        if (command.substring(0, bot.getPrefix().length()).equalsIgnoreCase(bot.getPrefix())) prefix = true;

        command = command.substring(bot.getPrefix().length());

        if (event.getMessage().getContentRaw().equals("69"))
            event.getPrivateChannel().sendMessage("nice").queue();
        else if (prefix)
            if (command.equalsIgnoreCase("porn"))
                event.getPrivateChannel().sendMessage("How about you look at this instead?\nhttps://i.imgur.com/Xm27PCH.jpg").queue();
            else if (command.equalsIgnoreCase("feet-pics"))
                event.getPrivateChannel().sendMessage("https://tenor.com/view/stop-it-get-some-help-gif-7929301").queue();
            else
                if (r.nextInt(10000) == 1 || (event.getMessage().getTimeCreated().getMonth() == Month.APRIL && event.getMessage().getTimeCreated().getDayOfMonth() == 1))
                    event.getPrivateChannel().sendMessage("Surprise!\nhttps://tenor.com/view/easter-easter-egg-jump-gif-8278876").queue();
                else
                    event.getPrivateChannel().sendMessage("You aren't going to find any easter eggs by dming me").queue();
    }

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        List<User> mentions = event.getMessage().getMentionedUsers();
        for (User m : mentions) {
            if (m.isBot() && m.getId().equals(self.getId())) {
                for (String swear : swearWords)
                    if (event.getMessage().getContentRaw().contains(swear)) {
                        event.getChannel().sendMessage(responses[r.nextInt(responses.length - 1)]).queue();
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
        if (event.getChannel().getName().equalsIgnoreCase("give-me-feet-pics"))
            if (r.nextInt(25) == 1)
                event.getChannel().sendMessage("ok fine, here are my feet pics http://shorturl.at/muyW9").queue();
            else
                event.getChannel().sendMessage("no.").queue();
        else if (event.getChannel().getName().equalsIgnoreCase("jesus-is-fire") || event.getChannel().getName().equalsIgnoreCase("read-the-bible"))
            event.getChannel().sendMessage("yes").queue();
    }
}
