package com.aelous.model.entity.combat.magic.data;

public enum ModernSpells {

    WIND_STRIKE(1152, 1, 2, 91, 1162, 51,43,31, 92);

    public final int spellID, levelReq, baseMaxHit, projectile, castAnimation, startSpeed, startHeight, endHeight, endGraphic;

    ModernSpells(int spellID, int levelReq, int baseMaxHit, int projectile, int castAnimation, int startSpeed, int startHeight, int endHeight, int endGraphic) {
        this.spellID = spellID;
        this.levelReq = levelReq;
        this.baseMaxHit = baseMaxHit;
        this.projectile = projectile;
        this.castAnimation = castAnimation;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.endGraphic = endGraphic;
    }

    public static ModernSpells find(int spellID) {
        if (spellID != -1) {
            for (ModernSpells moderns: ModernSpells.values()) {
                if (moderns.spellID == spellID) {
                    return moderns;
                }
            }
        }
        return null;
    }

}
