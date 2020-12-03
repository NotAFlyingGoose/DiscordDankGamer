package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.AttractInfo;
import com.runningmanstudios.discordlib.command.AttractableCommand;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.event.CommandEvent;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@CommandBuilder(name = "sell", description = "sell any item that is in the shop", usages = {"<item id>"})
public class SellCommand implements AttractableCommand {
    Map<String, String> orders = new HashMap<>();
    @Override
    public void onMessage(CommandEvent command) {
        DataBase items = command.getCommandManager().getBot().items;
        JSONObject shop = command.getCommandManager().getBot().data.getSection("shop");

        String item_id = command.getArg(0);

        if (items.getSection(item_id) == null) {
            command.reply("That is not a valid item!").queue();
            return;
        }

        JSONObject userData = command.getAuthorData();

        int itemCost = Integer.parseInt(shop.get(item_id).toString());

        if (!command.getCommandManager().getBot().doesUserHaveItem(command.getAuthor(), item_id)) {
            command.reply("You don't have that item!").queue();
            return;
        }

        command.reply("Are you sure you want to sell your `" + items.getSection(item_id).get("name").toString() + " - " + items.getSection(item_id).get("icon").toString() + "`? Type `Y` to confirm, `N` to cancel").queue();
        command.getCommandManager().setAttractor(command.getAuthor(), new AttractInfo(this).addAnswer(Pattern.compile(".*")));
        orders.put(command.getAuthor().getId(), item_id);
    }

    @Override
    public void onAttract(CommandEvent event) {

        if (event.getMessage().getContentRaw().equals("Y")) {
            DataBase items = event.getCommandManager().getBot().items;
            JSONObject shop = event.getCommandManager().getBot().data.getSection("shop");
            JSONObject userData = event.getAuthorData();

            String item_id = orders.get(event.getAuthor().getId());
            String purchased = items.getSection(item_id).get("name").toString() + " - " + items.getSection(item_id).get("icon").toString();
            int userCoins = Integer.parseInt(userData.get("coins").toString());
            int itemCost = Integer.parseInt(shop.get(item_id).toString());

            event.getCommandManager().getBot().giveUserItem(event.getAuthor(), item_id, 1);
            userData.put("coins", userCoins + (itemCost * 0.75));

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