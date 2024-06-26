package com.cryptic.model.entity.combat.ranged.drawback;

public enum ChinchompaDrawBack {
    CHINCHOMPA(10033, 157, 908,21,40,36, 5),
    RED_CHINCHOMPA(10034, 157, 909,21,40,36, 5),
    BLACK_CHINCHOMPA(11959, 157, 1272,21,40,36, 5);

    public final int id, gfx, projectile, startSpeed, startHeight, endHeight, stepMultiplier;

    ChinchompaDrawBack(int id, int gfx, int projectile, int startSpeed, int startHeight, int endHeight, int stepMultiplier) {
        this.id = id;
        this.gfx = gfx;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.stepMultiplier = stepMultiplier;
    }

    public static ChinchompaDrawBack find(int chin, int graphic) {
        if (graphic == -1 && chin != -1) {
            for (ChinchompaDrawBack chinchompaDrawBack : ChinchompaDrawBack.values()) {
                return chinchompaDrawBack;
            }
        }
        return null;
    }

}
