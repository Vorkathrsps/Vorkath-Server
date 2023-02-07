package com.aelous.services.discord;

import com.aelous.services.discord.channel.DiscordChannel;
import com.aelous.services.discord.listener.ChatListener;
import com.aelous.services.discord.listener.MemeberJoinListener;
import com.aelous.services.discord.listener.MemeberLeaveListener;
import com.aelous.utility.chainedwork.Chain;
import com.mysql.cj.protocol.MessageListener;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.EventDispatcher;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.guild.MemberLeaveEvent;
import discord4j.core.event.domain.message.MessageEvent;
import discord4j.core.object.presence.Activity;
import discord4j.core.object.presence.Presence;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ynneh | 04/04/2022 - 12:19
 * <https://github.com/drhenny>
 */
public class DiscordBot {

    public static final long GUILD_ID = 956723981501743104L;

    private static final String KEY_ID = "NzczMDIzOTkzODQxMDU3ODI0.X6DMsg.HJKwZYt79g9FT7HiP89KqRTc3D0";

    public static boolean isDiscordBotOnline;

    private static GatewayDiscordClient bot;

    public static Logger logger = Logger.getLogger(String.valueOf(DiscordBot.class));

    public static long start, finish;

    public static void init() {
        loadBot();
    }

    private static void loadBot() {
        logger.log(Level.INFO, "Starting DiscordBOT..");
        try {
            final DiscordClient client = DiscordClient.create(KEY_ID);
            bot = client.login().block();
            logger.log(Level.INFO, "Loading init login..");
            bot.updatePresence(Presence.doNotDisturb(Activity.watching("Aelous"))).block();
            logger.log(Level.INFO, "Loading presense..");
            loadListeners(bot.getEventDispatcher());
            logger.log(Level.INFO, "DiscordBOT is now online!");
            isDiscordBotOnline = true;
            bot.onDisconnect().block();
            start = System.currentTimeMillis();
        } catch (Exception e) {
            isDiscordBotOnline = false;
            System.err.println("Discord BOT failed to loadup! error trace below");
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message, DiscordChannel channel) {
        if (!isDiscordBotOnline || message == null)
            return;
        if (channel == null)
            channel = DiscordChannel.DEFAULT;
        bot.getChannelById(Snowflake.of(channel.uid)).block().getRestChannel().createMessage(message).block();
        System.err.println("sent message... "+message);
    }

    public static void updateStatus(String status) {
        bot.updatePresence(Presence.doNotDisturb(Activity.watching(status))).block();
    }

    private static void loadListeners(EventDispatcher eventDispatcher) {
        eventDispatcher.on(MessageEvent.class).subscribe(ChatListener::onMessageReceived);
        eventDispatcher.on(MemberJoinEvent.class).subscribe(MemeberJoinListener::onMessageReceived);
        eventDispatcher.on(MemberLeaveEvent.class).subscribe(MemeberLeaveListener::onMessageReceived);
    }

}
