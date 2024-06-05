package com.cryptic.model.entity.combat.magic.data;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public enum AncientSpells {

    HOME_TELEPORT(11142, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null),
    SMOKE_RUSH(4629, 12939, 384, 51, 43, 31, -1, 385, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SHADOW_RUSH(4630, 12987, 378, 51, 43, 0, -1, 379, 10, 1978, GraphicHeight.LOW, GraphicHeight.LOW),
    PADDEWWA_TELEPORT(4631, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null),
    BLOOD_RUSH(4632, 12901, 372, 51, 43, 0, -1, 373, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    ICE_RUSH(4633, 12861, 360, 51, 43, 0, -1, 361, 10, 1978, GraphicHeight.LOW, GraphicHeight.LOW),
    SENNTISTEN_TELEPORT(4634, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null),
    SMOKE_BURST(4635, 12963, -1, 51, 43, 31, -1, 389, 10, 1979, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SHADOW_BURST(4636, 13011, -1, 51, 43, 31, -1, 382, 10, 1979, GraphicHeight.LOW, GraphicHeight.LOW),
    KHARYLL_TELEPORT(4637, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null),
    BLOOD_BURST(4638, 12919, -1, 51, 43, 31, -1, 376, 10, 1979, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    ICE_BURST(4639, 12881, -1, 51, 43, 0, -1, 363, 10, 1979, GraphicHeight.LOW, GraphicHeight.LOW),
    LASSAR_TELEPORT(4640, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null),
    SMOKE_BLITZ(4641, 12951, 386, 51, 43, 31, -1, 387, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SHADOW_BLITZ(4642, 12999, 380, 51, 43, 0, -1, 381, 10, 1978, GraphicHeight.LOW, GraphicHeight.LOW),
    DAREEYAK_TELEPORT(4643, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null),
    BLOOD_BLITZ(4644, 12911, 374, 51, 43, 0, -1, 375, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    ICE_BLITZ(4645, 12871, -1, 51, 43, 0, 366, 367, 10, 1978, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    CARRALLANGER_TELEPORT(4646, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null),
    SMOKE_BARRAGE(4647, 12975, -1, 51, 43, 31, -1, 391, 10, 1979, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    SHADOW_BARRAGE(4647, 13023, -1, 51, 43, 31, -1, 383, 10, 1979, GraphicHeight.LOW, GraphicHeight.LOW),
    ANNAKARL_TELEPORT(4649, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null),
    BLOOD_BARRAGE(4650, 12929, -1, 51, 43, 31, -1, 377, 10, 1979, GraphicHeight.MIDDLE, GraphicHeight.MIDDLE),
    ICE_BARRAGE(4651, 12891, -1, 51, 43, 0, -1, 369, 10, 1979, GraphicHeight.LOW, GraphicHeight.LOW),
    GHORROCK_TELEPORT(4652, -1,-1,-1,-1,-1,-1,-1, -1, -1, null, null);

    public static final AncientSpells[] VALUES = values();
    public static final Int2ObjectMap<AncientSpells> ITEM_MAP = new Int2ObjectOpenHashMap<>();
    public static final Int2IntMap ANCIENT_SPELL_COMPONENT_MAP = new Int2IntOpenHashMap();

    public final int itemid, spellID, projectile, castAnimation, startSpeed, startHeight, endHeight, startGraphic, endGraphic, stepMultiplier;
    public final GraphicHeight startGraphicheight;
    public final GraphicHeight endGraphicHeight;

    static {
        for (var spell : VALUES) ITEM_MAP.put(spell.itemid, spell);
        for (var entry : AncientSpells.ITEM_MAP.int2ObjectEntrySet()) {
            var id = entry.getIntKey();
            ItemDefinition itemDefinition = ItemDefinition.cached.get(id);
            ANCIENT_SPELL_COMPONENT_MAP.put(Integer.parseInt(itemDefinition.params.get(596).toString()), entry.getValue().spellID);
        }
    }

    AncientSpells(int itemId, int spellID, int projectile, int startSpeed, int startHeight, int endHeight, int startGraphic, int endGraphic, int stepMultiplier, int castAnimation, GraphicHeight startGraphicheight, GraphicHeight endGraphicHeight) {
        this.itemid = itemId;
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

    public static AncientSpells findSpell(int spellID) {
        if (spellID != -1) {
            for (AncientSpells spell : VALUES) {
                if (spell.spellID == spellID) {
                    return spell;
                }
            }
        }
        return null;
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
