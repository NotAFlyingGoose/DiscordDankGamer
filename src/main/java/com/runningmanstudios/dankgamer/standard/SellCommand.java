package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.AttractorFactory;
import com.runningmanstudios.discordlib.command.AttractListener;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.Inventory;
import com.runningmanstudios.discordlib.data.MemberData;
import com.runningmanstudios.discordlib.event.BotMessageEvent;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;

@CommandBuilder(name = "sell", description = "sell any item that is in the shop", usages = {"<item id>"})
public class SellCommand implements Command, AttractListener {
    Map<String, String> orders = new HashMap<>();
    @Override
    public void onMessage(BotMessageEvent event) {
        Inventory inv = new Inventory(event.getBot(), event.getMemberData().inventory);
        JSONObject shop = (JSONObject) event.getBot().data.get("shop");

        String item_id = event.getArg(0);

        if (event.getBot().getItem(item_id) == null) {
            event.reply("That is not a valid item!").queue();
            return;
        }

        int itemCost = Integer.parseInt(shop.get(item_id).toString());

        if (!inv.hasItem(item_id)) {
            event.reply("You don't have that item!").queue();
            return;
        }

        event.reply("Are you sure you want to sell your `" + event.getBot().getItem(item_id).getName() + " - " + event.getBot().getItem(item_id).getIcon() + "`? Type `Y` to confirm, `N` to cancel").queue();
        event.getCommandManager().setAttractor(event.getAuthor(), AttractorFactory.createAnyAttractor(this));
        orders.put(event.getAuthor().getId(), item_id);
    }

    @Override
    public void onAttract(BotMessageEvent event) {
        if (event.getMessage().getContentRaw().equals("Y")) {
            JSONObject shop = (JSONObject) event.getBot().data.get("shop");
            MemberData userData = event.getMemberData();

            String item_id = orders.get(event.getAuthor().getId());
            String purchased = event.getBot().getItem(item_id).getName() + " - " + event.getBot().getItem(item_id).getIcon();
            int itemCost = Integer.parseInt(shop.get(item_id).toString());

            userData = userData.withCoins((int) (userData.coins + (itemCost * 0.75)));
            event.getBot().giveUserItem(userData, item_id, 1);

            InvoiceBuilder.createInvoice(event, purchased, itemCost).queue();

            event.getCommandManager().stopAttracting(event.getAuthor());
            orders.remove(event.getAuthor().getId());
        } else if (event.getMessage().getContentRaw().equals("N")) {
            event.reply("The order was cancelled").queue();
            event.getCommandManager().stopAttracting(event.getAuthor());
            orders.remove(event.getAuthor().getId());
        } else {
            event.reply("Incorrect response. Type `Y` to confirm the purchase or type `N` to cancel the purchase").queue();
        }

    }
}
