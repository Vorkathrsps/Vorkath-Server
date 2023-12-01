package com.cryptic.model.entity.player;

/**
 * @author Origin
 * juni 21, 2020
 */

public enum GameMode {

    TRAINED_ACCOUNT("Normal account",1, 150, 50),
    REALISM("realism", 2, 25, 10),
    HARDCORE_REALISM("hardcore realism", 3, 20, 15);

    private final String name;
    private final int uid;
    private final int combatExp;
    public final int multiplier;
    private static GameMode[] cache;

    GameMode(String name, int uid, int combatExp, int multiplier) {
        this.name = name;
        this.uid = uid;
        this.combatExp = combatExp;
        this.multiplier = multiplier;
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

    public boolean isDarklord() {
        return name.equals("Ironman");
    }

    public String toName() {
        return this.name;
    }
}
