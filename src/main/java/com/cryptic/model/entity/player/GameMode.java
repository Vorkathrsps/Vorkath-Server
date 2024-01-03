package com.cryptic.model.entity.player;

/**
 * @author Origin
 * juni 21, 2020
 */

public enum GameMode {

    TRAINED_ACCOUNT("Normal account",1, 25),
    REALISM("realism", 2, 10),
    HARDCORE_REALISM("hardcore realism", 3, 10);

    private final String name;
    private final int uid;
    public final int multiplier;

    GameMode(String name, int uid, int multiplier) {
        this.name = name;
        this.uid = uid;
        this.multiplier = multiplier;
    }

    public int uid() {
        return uid;
    }

    public boolean isIronman() {
        return name.equals("Ironman");
    }

    public String toName() {
        return this.name;
    }
}
