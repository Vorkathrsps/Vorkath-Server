package com.aelous.model.content.teleport;

/**
 * Created by Jak on 09/11/2016.
 */
public enum TeleportType {
    /**
     * Cannot be cast in combat
     */
    HOME_TELEPORT,
    /**
     * No usage
     */
    SPELLBOOK,
    /**
     * Works up to 30 wilderness (glories)
     */
    ABOVE_20_WILD,
    /**
     * Works up to 20 wilderness.
     */
    GENERIC,
}
