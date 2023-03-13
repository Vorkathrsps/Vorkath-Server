package com.aelous.services.discord.listener;

import com.aelous.services.discord.DiscordBot;
import com.aelous.services.discord.channel.DiscordChannel;
import com.aelous.services.discord.notification.NotificationType;
import com.aelous.services.security.Whitelist;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.MessageEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.event.domain.message.ReactionRemoveEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author Ynneh | 04/04/2022 - 12:31
 * <https://github.com/drhenny>
 */
public class ChatListener {

    /**
     * Event created to read messages on discord channels
     *
     * @param event
     */
    public static void onMessageReceived(MessageEvent event) {
        if (event instanceof MessageCreateEvent)
            readChannelText((MessageCreateEvent) event);
        else if (event instanceof ReactionAddEvent)
            handleReactionEvent((ReactionAddEvent) event);
        else if (event instanceof ReactionRemoveEvent)
            handleReactionRemove((ReactionRemoveEvent) event);
    }

    private static void handleReactionRemove(ReactionRemoveEvent event) {
        final User user = event.getUser().block();

        if (user == null)
            return;

        MessageChannel channel = event.getChannel().block();

        ReactionEmoji emoji = event.getEmoji();

        /**
         * NOT NEEDED YET.. for giveaways ect
         */

    }

    private static void handleReactionEvent(ReactionAddEvent event) {
        final User user = event.getUser().block();

        if (user == null)
            return;

        MessageChannel channel = event.getChannel().block();

        ReactionEmoji emoji = event.getEmoji();

        /**
         * NOT NEEDED YET.. for giveaways ect
         */
    }

    public static Optional<NotificationType> getData(String rawEmoji) {
        return Arrays.stream(NotificationType.values()).filter(d -> d.emojiToString.equalsIgnoreCase(rawEmoji)).findFirst();
    }

    private static void readChannelText(MessageCreateEvent event) {
        final Message message = event.getMessage();
        MessageChannel channel = message.getChannel().block();

        if (channel == null)
            return;

        String channelMessage = message.getContent();

        User author = message.getAuthor().get();

        System.out.println("NickName=" + author.getUsername() + " User=" + author.getTag() + " AvatarURL=" + author.getAvatarUrl() +" ID="+ author.getId()+" Message=" + channelMessage);

        if (channelMessage == null) {
            return;
        }


        boolean isCommand = channelMessage.startsWith("!");

        if (!isCommand)
            return;

        String[] formattedCommand = channelMessage.substring(1).split(" ");

        event.getMessage().delete().block();

        System.err.println("isAdmin=" + isAdministrator(author));

        try {

            switch (formattedCommand[0]) {

                case "test1":
                    DiscordBot.sendMessage("testing functionality", DiscordChannel.DEFAULT);
                    return;
            }

            if (!isAdministrator(author)) {
                return;
            }

            switch (formattedCommand[0]) {

                case "admintest": {
                    DiscordBot.sendMessage("You have admin perms..", DiscordChannel.DEFAULT);
                    return;
                }
            }

            if (!isOwner(author)) {
                DiscordBot.sendMessage("Only "+tagOwner()+" can use this command!", DiscordChannel.DEFAULT);
                return;
            }

            switch (formattedCommand[0]) {

                case "givemod": {
                    Whitelist.add(formattedCommand[1], false);
                    return;
                }

                case "giveadmin": {
                    Whitelist.add(formattedCommand[1], true);
                    return;
                }

                case "showstaff": {
                    Whitelist.showList();
                    return;
                }

                case "remove": {
                    Whitelist.remove(formattedCommand[1]);
                    return;
                }
            }
        } catch (Exception e) {
            System.err.println("no command?");
            e.printStackTrace();
        }
    }

    private static String tagOwner() {
        return "<@!637487779181232129>";
    }

    private static Optional<Snowflake> adminCheck(User author) {
        return author.asMember(Snowflake.of(DiscordBot.GUILD_ID)).block().getRoleIds().stream().filter(f -> f.asLong() == NotificationType.ADMIN.uid).findFirst();
    }

    public static boolean isAdministrator(User user) {
        if (isOwner(user))
            return true;
        Optional<Snowflake> a = adminCheck(user);

        if (a == null) {
            return false;
        }
        return a.isPresent();
    }

    public static boolean isOwner(User user) {
        return user.getTag().equalsIgnoreCase("BottleOvWater#8130");
    }
}
