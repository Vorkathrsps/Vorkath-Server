package com.cryptic.model.entity.player;

/**
 * @author Origin
 * juni 21, 2020
 */

public enum GameMode {

    TRAINED_ACCOUNT("Normal account",1, 20, 10, 1.0),
    REALISM("realism", 2, 10, 2, 1.08),
    HARDCORE_REALISM("hardcore realism", 3, 10, 2, 1.08);

    private final String name;
    private final int uid;
    public final int multiplier;
    public final int combatXp;
    public final double dropRate;

    GameMode(String name, int uid, int multiplier, int combatXp, double dropRate) {
        this.name = name;
        this.uid = uid;
        this.multiplier = multiplier;
        this.combatXp = combatXp;
        this.dropRate = dropRate;
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
