package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.AttractorFactory;
import com.runningmanstudios.discordlib.command.AttractListener;
import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.data.DataBase;
import com.runningmanstudios.discordlib.event.CommandEvent;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@CommandBuilder(name = "buy", description = "buy the items", usages = {"<item id>"})
public class BuyCommand implements Command, AttractListener {
    Map<String, String> orders = new HashMap<>();
    @Override
    public void onMessage(CommandEvent command) {
        DataBase items = command.getCommandManager().getBot().items;
        JSONObject shop = command.getCommandManager().getBot().data.getSection("shop");

        String item_id = command.getArg(0);

        JSONObject userData = command.getAuthorData();

        int userCoins = Integer.parseInt(userData.get("coins").toString());
        int itemCost = Integer.parseInt(shop.get(item_id).toString());

        if (itemCost > userCoins) {
            command.reply("You don't have enough money to buy that!").queue();
            return;
        }

        command.reply("Are you sure you want to buy `" + items.getSection(item_id).get("name").toString() + " - " + items.getSection(item_id).get("icon").toString() + "`? Type `Y` to confirm, `N` to cancel").queue();
        command.getCommandManager().setAttractor(command.getAuthor(), AttractorFactory.createAnyAttractor(this));
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
            userData.put("coins", userCoins - itemCost);

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
