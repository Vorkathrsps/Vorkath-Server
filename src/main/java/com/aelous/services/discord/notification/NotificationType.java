package com.aelous.services.discord.notification;

/**
 * @author Ynneh | 04/04/2022 - 12:33
 * <https://github.com/drhenny>
 */
public enum NotificationType {

    ADMIN(928063265530064906L, null, "<@&928063265530064906>"),

    ;

    public long uid;

    public String emojiToString, mentionAlias;

    NotificationType(long uid, String rawEmoji, String mentionAlias) {
        this.uid = uid;
        this.emojiToString = rawEmoji;
        this.mentionAlias = mentionAlias;
    }
}
