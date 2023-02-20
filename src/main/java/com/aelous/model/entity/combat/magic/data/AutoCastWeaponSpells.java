package com.aelous.model.entity.combat.magic.data;

import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public enum AutoCastWeaponSpells {

    TUMMEKENS_SHADOW(6, 2126, 51, 60, 31, 2125, 2127, 10, -1,  GraphicHeight.MIDDLE);

    public int spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;

    public GraphicHeight endGraphicHeight;
    AutoCastWeaponSpells(int spellID, int projectile, int startSpeed, int startHeight, int endHeight, int startGraphic, int endGraphic, int stepMultiplier, int castAnimation, GraphicHeight endGraphicHeight) {
        this.spellID = spellID;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startGraphic = startGraphic;
        this.endGraphic = endGraphic;
        this.stepMultiplier = stepMultiplier;
        this.castAnimation = castAnimation;
        this.endGraphicHeight = endGraphicHeight;
    }

    public static AutoCastWeaponSpells findSpellProjectileData(int spellID, GraphicHeight endGraphicHeight) {
        if (spellID != -1) {
            for (AutoCastWeaponSpells spell : AutoCastWeaponSpells.values()) {
                if (spell.spellID == spellID) {
                    return spell;
                }
            }
        }
        if (endGraphicHeight != null) {
            for (AutoCastWeaponSpells spell : AutoCastWeaponSpells.values()) {
                if (spell.endGraphicHeight == endGraphicHeight) {
                    return spell;
                }
            }
        }
        return null;
    }

}
