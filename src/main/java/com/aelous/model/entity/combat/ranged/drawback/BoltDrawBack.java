package com.aelous.model.entity.combat.ranged.drawback;

public enum BoltDrawBack {

    BONE_BOLT(8882, -1, 27,41,38,36, 5),
    BRONZE_BOLT(877, -1, 27,41,38,36, 5),
    OPAL_BOLT(879, -1, 27,41,38,36, 5),
    PEARL_BOLT(880, -1, 27,41,38,36, 5),
    BARBED_BOLT(881, -1, 27,41,38,36, 5),
    IRON_BOLT(9140, -1, 27,41,38,36, 5),
    STEEL_BOLT(9141, -1, 27,41,38,36, 5),
    MITH_BOLT(9142, -1, 27,41,38,36, 5),
    ADDY_BOLT(9143, -1, 27,41,38,36, 5),
    RUNE_BOLT(9144, -1, 27,41,38,36, 5),
    OPAL_E(9236, -1, 27,41,38,36, 5),
    JADE_E(9237, -1, 27,41,38,36, 5),
    PEAR_E(9238, -1, 27,41,38,36, 5),
    TOPAZ_E(9239, -1, 27,41,38,36, 5),
    SAPPHIRE_E(9240, -1, 27,41,38,36, 5),
    EMERALD_E(9241, -1, 27,41,38,36, 5),
    RUBY_E(9242, -1, 27,41,38,36, 5),
    DIAMOND_E(9243, -1, 27,41,38,36, 5),
    DRAG_E(9244, -1, 27,41,38,36, 5),
    ONYX_E(9245, -1, 27,41,38,36, 5),
    KEBBIT_BOLT(10158, 955, 27,41,38,36, 5),
    BOLT_RACK(4740, 993, 27,41,38,36, 5),
    DRAGON_BOLT(1468, -1, 27,41,38,36, 5);

    public final int bolt, gfx, projectile, startSpeed, startHeight, endHeight, stepMultiplier;

    BoltDrawBack(int bolt, int gfx, int projectile, int startSpeed, int startHeight, int endHeight, int stepMultiplier) {
        this.bolt = bolt;
        this.gfx = gfx;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.stepMultiplier = stepMultiplier;
    }

    public static BoltDrawBack find(int bolt, int graphic) {
        if (graphic == -1 && bolt != -1) {
            for (BoltDrawBack boltDrawBack : BoltDrawBack.values()) {
                if (boltDrawBack.bolt == bolt) {
                    return boltDrawBack;
                } else {
                        return boltDrawBack;
                    }
                }
            }
        return null;
    }

}
