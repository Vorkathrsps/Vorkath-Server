package com.aelous.model.entity.player;
/**
 * @author Patrick van Elderen | March, 06, 2021, 14:46
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public enum IronMode {

    NONE(""),

    REGULAR("Ironman"),
    HARDCORE("Hardcore Ironman"),
    ULTIMATE("Ultimate Ironman"),
    GROUP("Group Ironman");

    public final String name;

    IronMode(String name) {
        this.name = name;
    }

    public boolean ironman() {
        return name.equals("Ironman") || name.equals("Hardcore Ironman");
    }

    /**
     * Determines if the {@link #name} is equal to {@link IronMode#REGULAR}
     *
     * @return {@code true} if the player is of this type, otherwise {@code false}
     */
    public boolean isIronman() {
        return name.equals("Ironman");
    }

    /**
     * Determines if the {@link #name} is equal to {@link IronMode#ULTIMATE}
     *
     * @return {@code true} if the player is of this type, otherwise {@code false}
     */
    public boolean isUltimateIronman() {
        return name.equals("Ultimate Ironman");
    }

    /**
     * Determines if the {@link #name} is equal to {@link IronMode#HARDCORE}
     *
     * @return {@code true} if the player is of this type, otherwise {@code false}
     */
    public boolean isHardcoreIronman() {
        return name.equals("Hardcore Ironman");
    }

    /**
     * Determines if the {@link #name} is equal to {@link IronMode#GROUP}
     *
     * @return {@code true} if the player is of this type, otherwise {@code false}
     */
    public boolean isGroupIronman() {
        return name.equals("Group Ironman");
    }
}
