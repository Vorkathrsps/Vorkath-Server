package com.aelous.model.content.raids;

public class RaidStage {

    /**
     * Stage Check
     */
    private static int raidStage = 0;

    public RaidStage setRaidStage(int raidStage) {
        RaidStage.raidStage = raidStage;
        return this;
    }

    public static int getRaidStage() {
        return raidStage;
    }

}
