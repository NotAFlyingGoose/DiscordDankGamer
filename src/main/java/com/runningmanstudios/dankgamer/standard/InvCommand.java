package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.event.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.JSONObject;

import java.awt.*;

@CommandBuilder(name = "inv", description = "see your inventory")
public class InvCommand implements Command {
    @Override
    public void onMessage(CommandEvent command) {
        try {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle(command.getAuthor().getName())
                    .setColor(new Color(255, 0, 0));
            DataBase items = command.getCommandManager().getBot().items;
            JSONObject shop = command.getCommandManager().getBot().data.getSection("shop");
            JSONObject inv = (JSONObject) command.getCommandManager().getBot().getUserData(command.getAuthor()).get("inv");
            if (inv != null) {
                for (Object item : inv.keySet()) {
                    int amt = Integer.parseInt(inv.get(item.toString()).toString());
                    embed.addField(
                            items.getSection(item.toString()).get("name").toString(),
                            "**- Icon:** " + items.getSection(item.toString()).get("icon").toString() + " \n" +
                                    "**- Sell Price:** " + (shop.get(item) != null ? ((Number) shop.get(item)).intValue() * command.getCommandManager().getBot().getItemRarity(item.toString()) / 2 : 0) + " \n" +
                                    "**- Rarity:** " + command.getCommandManager().getBot().ItemRarityString(items.getSection(item.toString()).get("rarity").toString()) + " \n" +
                                    "**- Amount:** " + amt + " \n" +
                                    "**- Item id:** `" + item.toString() + "`", true);
                }
            } else {
                embed.setDescription("Your inventory is empty \uD83D\uDE33. Buy items from the shop or play games to create your own collection!");
            }

            command.getChannel().sendMessage(embed.build()).queue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
