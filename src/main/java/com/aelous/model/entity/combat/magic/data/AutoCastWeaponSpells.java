package com.aelous.model.entity.combat.magic.data;

import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public enum AutoCastWeaponSpells {
    TUMMEKENS_SHADOW(6, 2126, 51, 63, 31, 2125, 2127, 10, 9493,  GraphicHeight.HIGH),
    ACCURSED_SCEPTRE(7, 2337, 51, 43, 31, -1, 78, 10, 1167,  GraphicHeight.HIGH),
    SANGUINESTI_STAFF(3, 1539, 51, 31, 0, 1540, 1541, 10, 1167,  GraphicHeight.LOW),
    TRIDENT_OF_THE_SEAS(1, 1252, 51, 31, 0, 1251, 1253, 10, 1167,  GraphicHeight.LOW),
    TRIDENT_OF_THE_SWAMP(2, 1040, 51, 31, 0, 665, 1042, 10, 1167,  GraphicHeight.LOW);

    public final int spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;

    public final GraphicHeight endGraphicHeight;
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
