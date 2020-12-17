package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.event.BotMessageEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.requests.restaction.MessageAction;

import java.awt.*;

public class InvoiceBuilder {
    public static MessageAction createInvoice(BotMessageEvent event, String item, int price) {
        return event.getChannel().sendMessage(new EmbedBuilder()
                .setColor(new Color(55, 55, 75))
                .setAuthor("Invoice")
                .setTitle(event.getAuthor().getName() + " bought " + item)
                .setThumbnail("https://i.imgur.com/Vhtwgn0.jpg")
                .addField("They spent", price + "", true)
                .build());
    }
}
