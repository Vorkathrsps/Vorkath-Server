package com.aelous.model.entity.combat.ranged.drawback;

public enum ChinchompaDrawBack {
    CHINCHOMPA(10033, 157, 908,41,38,36),
    RED_CHINCHOMPA(10034, 157, 909,41,38,36),
    BLACK_CHINCHOMPA(11959, 157, 1272,41,38,36);

    public final int id, gfx, projectile, startSpeed, startHeight, endHeight;


    ChinchompaDrawBack(int id, int gfx, int projectile, int startSpeed, int startHeight, int endHeight) {
        this.id = id;
        this.gfx = gfx;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
    }

    public static ChinchompaDrawBack find(int bolt, int graphic) {
        if (bolt != -1) {
            for (ChinchompaDrawBack boltDrawBack : ChinchompaDrawBack.values()) {
                if (boltDrawBack.id == bolt) {
                    return boltDrawBack;
                } else {
                    return boltDrawBack;
                }
            }
        }
        return null;
    }

}
