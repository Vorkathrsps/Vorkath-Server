package com.aelous.services.discord.listener;

import discord4j.core.event.domain.guild.MemberLeaveEvent;

/**
 * @author Ynneh | 04/04/2022 - 12:31
 * <https://github.com/drhenny>
 */
public class MemeberLeaveListener {

    public static void onMessageReceived(MemberLeaveEvent event) {
        System.err.println("memberleave---"+event.toString());
    }
}
