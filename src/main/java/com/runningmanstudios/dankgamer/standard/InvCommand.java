package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.Inventory;
import com.runningmanstudios.discordlib.data.Item;
import com.runningmanstudios.discordlib.data.MemberData;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.JSONObject;

import java.awt.*;

@CommandBuilder(name = "inv", description = "see your inventory")
public class InvCommand implements Command {
    @Override
    public void onMessage(BotMessageEvent command) {
        try {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(command.getAuthor().getName())
                    .setColor(new Color(255, 0, 0));
            JSONObject shop = (JSONObject) command.getCommandManager().getBot().data.get("shop");
            MemberData userData = command.getMemberData();
            Inventory inv = new Inventory(command.getCommandManager().getBot(), userData.inventory);

            if (inv.getItems().isEmpty()) {
                embed.setDescription("Your inventory is empty :frowning2:");
            }

            for (Item item : inv.getItems().keySet()) {
                embed.addField(
                        item.getName(),
                        "**- Icon:** " + item.getIcon() + " \n" +
                                "**- Sell Price:** " + (shop.get(item) != null ? ((Number) shop.get(item)).intValue() * item.getRarity() / 2 : 0) + " \n" +
                                "**- Rarity:** " + item.getRarityString() + " \n" +
                                "**- Amount:** " + inv.getItems().get(item) + " \n" +
                                "**- Item id:** `" + item.getId() + "`", true);
            }

            command.getChannel().sendMessage(embed.build()).queue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
