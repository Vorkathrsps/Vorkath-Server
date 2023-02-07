package com.aelous.services.discord.listener;

import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.object.entity.Member;

/**
 * @author Ynneh | 04/04/2022 - 12:30
 * <https://github.com/drhenny>
 */
public class MemeberJoinListener {

    public static void onMessageReceived(MemberJoinEvent message) {
        boolean isBot = message.getMember().isBot();
        final Member member = message.getMember();


        System.err.println("MemeberJoinListener---"+message.toString());
    }
}
