package com.runningmanstudios.dankgamer.standard;

import com.runningmanstudios.discordlib.command.Command;
import com.runningmanstudios.discordlib.command.CommandBuilder;
import com.runningmanstudios.discordlib.event.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@CommandBuilder(name = "avatar",
        description = "find your avatar",
        usages = {"", "<user>"},
        aliases = {"icon", "pfp"})
public class AvatarCommand implements Command {
    Random r = new Random();

    @Override
    public void onMessage(CommandEvent event) {
        List<Member> members = event.getMessage().getMentionedMembers();
        if (members.isEmpty()) {
            /*EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(0, 155, 255))
                    .setTitle("Your avatar")
                    .setImage(event.getAuthor().getAvatarUrl());
            event.getChannel().sendMessage(embed.build()).queue();*/

            BufferedImage image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2d = (Graphics2D) image.getGraphics();

            g2d.setRenderingHints(new RenderingHints(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
            g2d.setRenderingHints(new RenderingHints(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON));

            String fontName = g2d.getFont().getName();

            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, image.getWidth(), image.getHeight());

            g2d.setFont(new Font(fontName, Font.PLAIN, 18));

            g2d.setColor(Color.WHITE);
            g2d.drawString("Hello " + event.getAuthor().getName() + ", nice to see you again!", 10, 10 + g2d.getFontMetrics().getAscent());

/*            String html = """
                    <!DOCTYPE html>

                    <html>
                        <style>
                            html {
                                background: black;
                            }
                            body {
                                margin: 0;
                                background: gray;
                                font-family: Arial, Helvetica, sans-serif;
                            }
                        </style>

                        <h1>Hello ${USER}, nice to see you again!</h1>
                        <p>
                            I know that you've been gone for a while and I just wanted to show you this thing that I am working on which is basically sending a custom made image over to you!
                            That's right, I made this image with <b>you</b>, ${USER}, in mind. That being said here is your avatar:
                        </p>
                        <img src="${USER_PFP}">
                    </html>
                    """
                    .replaceAll("\\$\\{USER}", event.getAuthor().getName())
                    .replaceAll("\\$\\{USER_PFP}", Objects.requireNonNull(event.getAuthor().getAvatarUrl()));

            System.out.println(event.getAuthor().getAvatarUrl());
            JEditorPane jEditorPane = new JEditorPane();

            HTMLEditorKit kit = new HTMLEditorKit();

            StyleSheet ss = new StyleSheet();
            ss.addRule("body { " +
                       "margin: 0px; " +
                       "background: gray; " +
                       "font-family: Arial, Helvetica, sans-serif; " +
                       "}");
            ss.addStyleSheet(kit.getStyleSheet());
            kit.setStyleSheet(ss);

            jEditorPane.setEditorKit(kit);
            jEditorPane.setSize(400, 300);
            jEditorPane.setContentType("text/html");
            jEditorPane.setText(html);
            jEditorPane.print(g2d);*/

            event.sendImage(image, "test.png").queue();

        } else {
            Member member = members.get(0);
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(new Color(0, 155, 255))
                    .setTitle(member.getUser().getName()+"'s avatar")
                    .setImage(member.getUser().getAvatarUrl());
            event.getChannel().sendMessage(embed.build()).queue();
        }
    }
}
