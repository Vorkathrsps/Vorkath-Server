package com.cryptic.model.entity.combat.magic.data;

import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public enum ModernSpells {

    WIND_STRIKE(1152, 91, 51, 43, 31, 90, 92, 10, 1162, GraphicHeight.HIGH),
    WATER_STRIKE(1154, 94, 51, 43, 31, 93, 95, 10, 1162, GraphicHeight.HIGH),
    EARTH_STRIKE(1156, 97, 51, 43, 31, 96, 98, 10, 1162, GraphicHeight.HIGH),
    FIRE_STRIKE(1158, 100, 51, 43, 31, 99, 101, 10, 1162, GraphicHeight.HIGH),
    WIND_BOLT(1160, 118, 51, 43, 31, 117, 119, 10, 1162, GraphicHeight.HIGH),
    WATER_BOLT(1163, 121, 51, 43, 31, 120, 122, 10, 1162, GraphicHeight.HIGH),
    EARTH_BOLT(1166, 124, 51, 43, 31, 123, 125, 10, 1162, GraphicHeight.HIGH),
    FIRE_BOLT(1169, 127, 51, 43, 31, 126, 128, 10, 1162, GraphicHeight.HIGH),
    WIND_BLAST(1172, 133, 51, 43, 31, 132, 134, 10, 1162, GraphicHeight.HIGH),
    WATER_BLAST(1175, 136, 51, 43, 31, 135, 137, 10, 1162, GraphicHeight.HIGH),
    EARTH_BLAST(1177, 139, 51, 43, 31, 138, 140, 10, 1162, GraphicHeight.HIGH),
    FIRE_BLAST(1181, 130, 51, 43, 31, 129, 131, 10, 1162, GraphicHeight.HIGH),
    SARADOMIN_STRIKE(1190, -1, 51, 43, 31, -1, 76, 10, 811, GraphicHeight.HIGH),
    CLAWS_OF_GUTHIX(1191, -1, 51, 43, 31, -1, 77, 10, 811, GraphicHeight.HIGH),
    FLAMES_OF_ZAMORAK(1192, -1, 51, 43, 31, -1, 78, 10, 811, GraphicHeight.HIGH),
    WIND_WAVE(1183, 159, 51, 43, 31, 158, 160, 10, 1167, GraphicHeight.HIGH),
    WATER_WAVE(1185, 162, 51, 43, 31, 161, 163, 10, 1167, GraphicHeight.HIGH),
    EARTH_WAVE(1188, 165, 51, 43, 31, 164, 166, 10, 1167, GraphicHeight.HIGH),
    FIRE_WAVE(1189, 156, 51, 43, 31, 155, 157, 10, 1167, GraphicHeight.HIGH),
    AIR_SURGE(22708, 1456, 51, 43, 31, 1455, 1457, 10, 7855, GraphicHeight.HIGH),
    WATER_SURGE(22658, 1459, 51, 43, 31, 1458, 1460, 10, 7855, GraphicHeight.HIGH),
    EARTH_SURGE(22628, 1462, 51, 43, 31, 1461, 1463, 10, 7855, GraphicHeight.HIGH),
    FIRE_SURGE(22608, 1465, 51, 43, 31, 1464, 1466, 10, 7855, GraphicHeight.HIGH),
    SNARE(1582, 178, 75, 43, 0, 177, 180, 10, 1161, GraphicHeight.LOW),
    VULNERABILITY(1542, 168, 34, 36, 31, 167, 169, 10, 718, GraphicHeight.LOW),
    MAGIC_DART(12037, 328, 51, 43, 31, -1, 329, 10, 1576, GraphicHeight.LOW),
    IBAN_BLAST(1539, 89, 51, 36, 31, 87, 89, 10, 708, GraphicHeight.LOW),
    BIND(1572, 178, 75, 45, 0, 177, 181, 10, 1161, GraphicHeight.LOW),
    CURSE(1161, 109, 51, 43, 31, 108, 110, 10, 1165, GraphicHeight.LOW),
    WEAKEN(1157, 106, 44, 36, 31, 105, 107, 10, 1164, GraphicHeight.HIGH),
    CONFUSE(1153, 103, 61, 36, 31, 102, 104, 10, 1163, GraphicHeight.HIGH),
    CRUMBLE_UNDEAD(1171, 146, 46, 31, 31, 145, 147, 10, 724, GraphicHeight.LOW),
    ENFEEBLE(1543, 171, 48, 36, 31, 170, 172, 10, 728, GraphicHeight.LOW),
    STUN(1562, 174, 52, 36, 31, 173, 80, 10, 729, GraphicHeight.LOW),
    ENTANGLE(1592, 178, 75, 43, 0, 177, 180, 10, 1161, GraphicHeight.LOW),
    TELEBLOCK(12445, 1299, 75, 43, 31, -1, 345, 10, 1820, GraphicHeight.LOW);

    public final int spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;

    public final GraphicHeight endGraphicHeight;

    ModernSpells(int spellID, int projectile, int startSpeed, int startHeight, int endHeight, int startGraphic, int endGraphic, int stepMultiplier, int castAnimation, GraphicHeight endGraphicHeight) {
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

    public int getProjectile() {
        return projectile;
    }

    public int getStartHeight() {
        return startHeight;
    }

    public int getEndHeight() {
        return endHeight;
    }

    public static ModernSpells findSpellProjectileData(int spellID, GraphicHeight endGraphicHeight) {
        if (spellID != -1) {
            for (ModernSpells spell : ModernSpells.values()) {
                if (spell.spellID == spellID) {
                    return spell;
                }
            }
        }
        if (endGraphicHeight != null) {
            for (ModernSpells spell : ModernSpells.values()) {
                if (spell.endGraphicHeight == endGraphicHeight) {
                    return spell;
                }
            }
        }
        return null;
    }
}
