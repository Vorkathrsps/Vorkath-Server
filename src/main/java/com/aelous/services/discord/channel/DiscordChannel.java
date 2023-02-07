package com.aelous.services.discord.channel;

/**
 * @author Ynneh | 04/04/2022 - 12:19
 * <https://github.com/drhenny>
 */
public enum DiscordChannel {

    DEFAULT(960499068449218590L),
    ADMIN(960502807721541643L),

    ;

    public long uid;

    DiscordChannel(long uid) {
        this.uid = uid;
    }
}
