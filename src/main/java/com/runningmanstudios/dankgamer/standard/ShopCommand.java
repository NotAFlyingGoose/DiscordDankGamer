package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.Item;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.json.simple.JSONObject;

import java.awt.*;

@CommandBuilder(name = "shop", description = "shop for buying items")
public class ShopCommand implements Command {
    @Override
    public void onMessage(BotMessageEvent event) {
        try {
            EmbedBuilder embed = new EmbedBuilder()
                    .setTitle("Shopping Center")
                    .setColor(new Color(255, 0, 0))
                    .setFooter("buy an item with `" + event.getBot().getPrefix() + "buy <item id>`");
            JSONObject shop = (JSONObject) event.getBot().data.get("shop");
            for (Object field : shop.keySet()) {
                Item item = event.getBot().getItem(field.toString());
                embed.addField(
                        item.getName(),
                        "**- Icon:** " + item.getIcon() + " \n" +
                                "**- Price:** " + (Integer.parseInt(shop.get(item.getId()).toString()) * item.getRarity()) + " \n" +
                                "**- Rarity:** " + item.getRarityString() + " \n" +
                                "**- Item id:** `" + field + "`", true);
            }

            event.getChannel().sendMessage(embed.build()).queue();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
