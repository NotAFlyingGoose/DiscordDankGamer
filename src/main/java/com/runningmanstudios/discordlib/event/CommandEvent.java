package com.runningmanstudios.discordlib.event;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.json.simple.JSONObject;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CommandEvent extends GuildMessageReceivedEvent {
    String[] args;
    CommandManager commandManager;

    public CommandEvent(GuildMessageReceivedEvent message, String[] args, CommandManager commandManager) {
        super(message.getJDA(), message.getResponseNumber(), message.getMessage());
        this.args = args;
        this.commandManager = commandManager;
    }

    public JSONObject getAuthorData() {
        return commandManager.getBot().getUserData(getAuthor().getId());
    }

    public String[] getArgs() {
        return args;
    }

    public String getArg(int index) {
        return args[index];
    }

    @Nonnull
    @CheckReturnValue
    public MessageAction reply(@Nonnull CharSequence message)
    {
        return getChannel().sendMessage(createMention(getAuthor()) + ", " + message);
    }

    @Nonnull
    @CheckReturnValue
    public MessageAction reply(@Nonnull Message message)
    {
        return getChannel().sendMessage(createMention(getAuthor()) + ", " + message.getContentRaw());
    }

    public MessageAction sendImage(@Nonnull BufferedImage img, String name) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            //File file = new File("temp." + type); // change the '.jpg' to whatever extension the image has
            ImageIO.write(img, name.split("\\.")[name.split("\\.").length - 1], baos); // again, change 'jpg' to the correct extension
            return getChannel().sendFile(baos.toByteArray(), name);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String createMention(User user) {
        return "<@!"+user.getId()+">";
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }
}
