package com.aelous.model.entity.player;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 21, 2020
 */
public enum GameMode {

    TRAINED_ACCOUNT("Normal account",1, 150),
    DARK_LORD("Dark lord", 2,125);

    private final String name;
    private final int uid;
    private final int combatExp;
    private static GameMode[] cache;

    GameMode(String name, int uid, int combatExp) {
        this.name = name;
        this.uid = uid;
        this.combatExp = combatExp;
    }

    public int uid() {
        return uid;
    }

    public int combatXpRate() {
        return combatExp;
    }

    public static GameMode forUid(int uid) {
        if (cache == null) {
            cache = values();
        }

        for (GameMode m : cache) {
            if (m.uid == uid)
                return m;
        }

        return null;
    }

    /**
     * Determines if the {@link #name} is equal to {@link GameMode#DARK_LORD}
     *
     * @return {@code true} if the player is of this type, otherwise {@code false}
     */
    public boolean isDarklord() {
        return name.equals("Ironman");
    }

    public String toName() {
        return this.name;
    }
}
