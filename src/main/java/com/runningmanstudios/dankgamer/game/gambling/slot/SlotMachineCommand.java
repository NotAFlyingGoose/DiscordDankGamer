package com.runningmanstudios.dankgamer.game.gambling.slot;

import com.runningmanstudios.discordlib.Util;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Random;

@CommandBuilder(name = "slot-machine", description = "Play the slots!", usages = "<bet amount>", aliases = {"slots", "slot"})
public class SlotMachineCommand implements Command {
    private static final int MIN_BET = 25;
    private static final int MAX_BET = 500;
    private static final Random random = new Random();
    private static final String[] slots = new String[] {"7️⃣", "💯", "💰", "🍒", "🍀", "🍄", "⭐", "🌀", "👽", "🚀"};

    @Override
    public void onMessage(BotMessageEvent event) {
        int betting = Integer.parseInt(event.getArg(0));
        if (betting < SlotMachineCommand.MIN_BET || betting > SlotMachineCommand.MAX_BET) {
            event.reply("The minimum you can bet is " + SlotMachineCommand.MIN_BET + ", and the max is " + SlotMachineCommand.MAX_BET).queue();
            return;
        }
        int userCoins = Util.getUserCoins(event.getGuild().getId(), event.getAuthor().getId());
        if (userCoins < betting) {
            event.reply("You don't have the funds to play :frowning2:\nMaybe you should come back later").queue();
            return;
        }
        EmbedBuilder machine = new EmbedBuilder();
        machine.setTitle("Slot Machine");
        machine.setDescription("Gambling Addiction is a serious problem, if you or anyone you know has a problem find help here: https://www.addictioncenter.com/drugs/gambling-addiction/.");
        machine.setFooter(event.getAuthor().getAsTag());

        int[] codes = new int[3];
        for (int i = 0; i < codes.length; i++)
            codes[i] = random.nextInt(9);

        StringBuilder slotResult = new StringBuilder();

        StringBuilder top = new StringBuilder("⬛ ");
        StringBuilder middle = new StringBuilder("▶️ ");
        StringBuilder bottom = new StringBuilder("⬛ ");
        for (int i = 0; i < codes.length; i++) {
            int code = codes[i];
            int upCode = code - 1;
            int downCode = code + 1;
            if (upCode == -1) upCode = 9;
            if (downCode == 10) upCode = 0;

            top.append(slots[upCode]);
            middle.append(slots[code]);
            bottom.append(slots[downCode]);

            if (i != codes.length - 1) {
                top.append(" | ");
                middle.append(" | ");
                bottom.append(" | ");
            }
        }
        top.append(" ⬛");
        middle.append(" ◀️");
        bottom.append(" ⬛");

        slotResult.append(top).append('\n').append(middle).append('\n').append(bottom);

        int matching = 0;
        boolean wild = false;
        for (int i = 0; i < codes.length; i++) {
            int code = codes[i];
            for (int j = 0; j < codes.length; j++) {
                if (i == j) continue;
                int c = codes[j];

                if (c == 0 || code == 0)
                    wild = true;

                if (code == c) {
                    matching++;
                }
            }
        }

        if (matching == codes.length) {
            int payout = matching * betting;
            if (wild) {
                payout += 2;
                machine.setDescription("**💰\uD83C\uDF1F💰 DOUBLE JACKPOT! 💰\uD83C\uDF1F💰\nYou Got \uD83E\uDE99"+payout+"**");
            } else {
                machine.setDescription("**💰 JACKPOT 💰\nYou Got \uD83E\uDE99"+payout+"**");
            }
            Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), userCoins + payout);
        } else if (matching == 0) {
            machine.setDescription("**You Lost \uD83E\uDE99"+betting+" :sob:**");
            Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), userCoins - betting);

        } else {
            int payout = matching * betting;
            machine.setDescription("**You Got \uD83E\uDE99"+payout+"**");
            Util.setUserCoins(event.getGuild().getId(), event.getAuthor().getId(), userCoins + payout);
        }

        machine.addField("Result", slotResult.toString(), false);

        event.getChannel().sendMessage(machine.build()).queue();
    }
}
