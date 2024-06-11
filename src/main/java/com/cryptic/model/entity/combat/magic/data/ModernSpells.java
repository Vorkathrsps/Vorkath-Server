package com.cryptic.model.entity.combat.magic.data;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum ModernSpells {

    LUMBRIDGE_HOME_TELEPORT(9712, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    WIND_STRIKE(3273, 1152, 91, 51, 43, 31, 90, 92, 10, 1162, GraphicHeight.HIGH),
    WATER_STRIKE(3275, 1154, 94, 51, 43, 31, 93, 95, 10, 1162, GraphicHeight.HIGH),
    EARTH_STRIKE(3277, 1156, 97, 51, 43, 31, 96, 98, 10, 1162, GraphicHeight.HIGH),
    FIRE_STRIKE(3279, 1158, 100, 51, 43, 31, 99, 101, 10, 1162, GraphicHeight.HIGH),
    WIND_BOLT(3281, 1160, 118, 51, 43, 31, 117, 119, 10, 1162, GraphicHeight.HIGH),
    WATER_BOLT(3285, 1163, 121, 51, 43, 31, 120, 122, 10, 1162, GraphicHeight.HIGH),
    VARROCK_TELEPORT(3286, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    EARTH_BOLT(3288, 1166, 124, 51, 43, 31, 123, 125, 10, 1162, GraphicHeight.HIGH),
    LUMBRIDGE_TELEPORT(3289, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    FIRE_BOLT(3291, 1169, 127, 51, 43, 31, 126, 128, 10, 1162, GraphicHeight.HIGH),
    FALADOR_TELEPORT(3292, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    WIND_BLAST(3294, 1172, 133, 51, 43, 31, 132, 134, 10, 1162, GraphicHeight.HIGH),
    CAMELOT_TELEPORT(3296, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    WATER_BLAST(3297, 1175, 136, 51, 43, 31, 135, 137, 10, 1162, GraphicHeight.HIGH),
    EARTH_BLAST(3302, 1177, 139, 51, 43, 31, 138, 140, 10, 1162, GraphicHeight.HIGH),
    FIRE_BLAST(3307, 1181, 130, 51, 43, 31, 129, 131, 10, 1162, GraphicHeight.HIGH),
    SARADOMIN_STRIKE(3311, 1190, -1, 51, 43, 31, -1, 76, 10, 811, GraphicHeight.HIGH),
    CLAWS_OF_GUTHIX(3309, 1191, -1, 51, 43, 31, -1, 77, 10, 811, GraphicHeight.HIGH),
    FLAMES_OF_ZAMORAK(3310, 1192, -1, 51, 43, 31, -1, 78, 10, 811, GraphicHeight.HIGH),
    WIND_WAVE(3313, 1183, 159, 51, 43, 31, 158, 160, 10, 1167, GraphicHeight.HIGH),
    WATER_WAVE(3315, 1185, 162, 51, 43, 31, 161, 163, 10, 1167, GraphicHeight.HIGH),
    EARTH_WAVE(3319, 1188, 165, 51, 43, 31, 164, 166, 10, 1167, GraphicHeight.HIGH),
    FIRE_WAVE(3321, 1189, 156, 51, 43, 31, 155, 157, 10, 1167, GraphicHeight.HIGH),
    AIR_SURGE(21876, 22708, 1456, 51, 43, 31, 1455, 1457, 10, 7855, GraphicHeight.HIGH),
    WATER_SURGE(21877, 22658, 1459, 51, 43, 31, 1458, 1460, 10, 7855, GraphicHeight.HIGH),
    EARTH_SURGE(21878, 22628, 1462, 51, 43, 31, 1461, 1463, 10, 7855, GraphicHeight.HIGH),
    FIRE_SURGE(21879, 22608, 1465, 51, 43, 31, 1464, 1466, 10, 7855, GraphicHeight.HIGH),
    SNARE(3300, 1582, 178, 75, 43, 0, 177, 180, 10, 1161, GraphicHeight.LOW),
    VULNERABILITY(3317, 1542, 168, 34, 36, 31, 167, 169, 10, 718, GraphicHeight.LOW),
    MAGIC_DART(4176, 12037, 328, 51, 43, 31, -1, 329, 10, 1576, GraphicHeight.LOW),
    ARDOUGNE_TELEPORT(3301, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    IBAN_BLAST(3299, 1539, 89, 51, 36, 31, 87, 89, 10, 708, GraphicHeight.LOW),
    BIND(3283, 1572, 178, 75, 45, 0, 177, 181, 10, 1161, GraphicHeight.LOW),
    CURSE(3282, 1161, 109, 51, 43, 31, 108, 110, 10, 1165, GraphicHeight.LOW),
    WEAKEN(3278, 1157, 106, 44, 36, 31, 105, 107, 10, 1164, GraphicHeight.HIGH),
    CONFUSE(3274, 1153, 103, 61, 36, 31, 102, 104, 10, 1163, GraphicHeight.HIGH),
    CRUMBLE_UNDEAD(3293, 1171, 146, 46, 31, 31, 145, 147, 10, 724, GraphicHeight.LOW),
    ENFEEBLE(3320, 1543, 171, 48, 36, 31, 170, 172, 10, 728, GraphicHeight.LOW),
    STUN(3324, 1562, 174, 52, 36, 31, 173, 80, 10, 729, GraphicHeight.LOW),
    ENTANGLE(3322, 1592, 178, 75, 43, 0, 177, 180, 10, 1161, GraphicHeight.LOW),
    TELEBLOCK(4555, 12445, 1299, 75, 43, 31, -1, 345, 10, 1820, GraphicHeight.LOW),
    WATCHTOWER_TELEPORT(3306, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    TROLLHEIM_TELEPORT(3312, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    APE_ATOLL_TELEPORT(7619, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    KOUREND_TELEPORT(21836, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH),
    CIVITAS_ILLA_FORTIS_TELEPORT(24612, -1, -1, -1, -1, -1, -1, -1, -1, -1, GraphicHeight.HIGH);

    public final int itemId, spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;

    public static final ModernSpells[] VALUES = values();
    public final GraphicHeight endGraphicHeight;
    public static final Int2ObjectMap<ModernSpells> ITEM_MAP = new Int2ObjectOpenHashMap<>();
    public static final Int2IntMap MODERN_SPELL_COMPONENT_MAP = new Int2IntOpenHashMap();

    static {
        for (var spell : VALUES) ITEM_MAP.put(spell.itemId, spell);
        for (var entry : ModernSpells.ITEM_MAP.int2ObjectEntrySet()) {
            var id = entry.getIntKey();
            ItemDefinition itemDefinition = ItemDefinition.getInstance(id);
            MODERN_SPELL_COMPONENT_MAP.put(Integer.parseInt(itemDefinition.params.get(596).toString()), entry.getValue().spellID);
        }
    }

    ModernSpells(int itemId, int spellID, int projectile, int startSpeed, int startHeight, int endHeight, int startGraphic, int endGraphic, int stepMultiplier, int castAnimation, GraphicHeight endGraphicHeight) {
        this.itemId = itemId;
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

    public static ModernSpells findSpell(int spellID) {
        if (spellID != -1) {
            for (ModernSpells spell : ModernSpells.values()) {
                if (spell.spellID == spellID) {
                    return spell;
                }
            }
        }
        return null;
    }

    public static ModernSpells findSpell(int spellID, GraphicHeight endGraphicHeight) {
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
