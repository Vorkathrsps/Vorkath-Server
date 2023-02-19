package com.aelous.model.entity.combat.magic.data;

import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public enum AncientSpells {

    SMOKE_RUSH(12939, 384, 51, 43, 31, -1, 385, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SHADOW_RUSH(12987, 378, 51, 43, 0, -1, 379, 10, 1978, GraphicHeight.LOW, GraphicHeight.LOW),
    BLOOD_RUSH(12901, 372, 51, 43, 0, -1, 373, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    ICE_RUSH(12861, 360, 51, 43, 0, -1, 361, 10, 1978, GraphicHeight.LOW, GraphicHeight.LOW),
    SMOKE_BURST(12963, -1, 51, 43, 31, -1, 389, 10, 1979, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SHADOW_BURST(13011, -1, 51, 43, 31, -1, 382, 10, 1979, GraphicHeight.LOW, GraphicHeight.LOW),
    BLOOD_BURST(12919, -1, 51, 43, 31, -1, 376, 10, 1979, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    ICE_BURST(12881, -1, 51, 43, 0, -1, 363, 10, 1979, GraphicHeight.LOW, GraphicHeight.LOW),
    SMOKE_BLITZ(12951, 386, 51, 43, 31, -1, 387, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SHADOW_BLITZ(12999, 380, 51, 43, 0, -1, 381, 10, 1978, GraphicHeight.LOW, GraphicHeight.LOW),
    BLOOD_BLITZ(12911, 374, 51, 43, 0, -1, 375, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    ICE_BLITZ(12871, -1, 51, 43, 0, 366, 367, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SMOKE_BARRAGE(12975, -1, 51, 43, 31, -1, 391, 10, 1979, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SHADOW_BARRAGE(13023, -1, 51, 43, 31, -1, 383, 10, 1979, GraphicHeight.LOW, GraphicHeight.LOW),
    BLOOD_BARRAGE(12929, -1, 51, 43, 31, -1, 377, 10, 1979, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    ICE_BARRAGE(12891, -1, 51, 43, 0, -1, 369, 10, 1979,  GraphicHeight.LOW, GraphicHeight.LOW);

    public int spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;

    public GraphicHeight startGraphicheight;
    public GraphicHeight endGraphicHeight;
    AncientSpells(int spellID, int projectile, int startSpeed, int startHeight, int endHeight, int startGraphic, int endGraphic, int stepMultiplier, int castAnimation, GraphicHeight startGraphicheight, GraphicHeight endGraphicHeight) {
        this.spellID = spellID;
        this.projectile = projectile;
        this.startSpeed = startSpeed;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.startGraphic = startGraphic;
        this.endGraphic = endGraphic;
        this.stepMultiplier = stepMultiplier;
        this.castAnimation = castAnimation;
        this.startGraphicheight = startGraphicheight;
        this.endGraphicHeight = endGraphicHeight;
    }

    public int getProjectile() {
        return projectile;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public static AncientSpells findSpellProjectileData(int spellID, GraphicHeight startGraphicHeight, GraphicHeight endGraphicHeight) {
        if (spellID != -1) {
            for (AncientSpells spell : AncientSpells.values()) {
                if (spell.spellID == spellID) {
                    return spell;
                }
            }
        }
        if (startGraphicHeight != null) {
            for (AncientSpells spell : AncientSpells.values()) {
                if (spell.startGraphicheight == startGraphicHeight) {
                    return spell;
                }
            }
        }
        if (endGraphicHeight != null) {
            for (AncientSpells spell : AncientSpells.values()) {
                if (spell.endGraphicHeight == endGraphicHeight) {
                    return spell;
                }
            }
        }
        return null;
    }
}
