package com.cryptic.model.content.items.loot.impl.caskets;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class EliteCasket implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.ROYAL_CROWN, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ROYAL_GOWN_TOP, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ROYAL_GOWN_BOTTOM, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ROYAL_SCEPTRE, 50, -1, false),
                new CollectionItem(ItemIdentifiers.MUSKETEER_HAT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.MUSKETEER_TABARD, 50, -1, false),
                new CollectionItem(ItemIdentifiers.MUSKETEER_PANTS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DHIDE_CHAPS_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DHIDE_BODY_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DHIDE_BODY_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DHIDE_CHAPS_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.RANGERS_TUNIC, 50, -1, false),
                new CollectionItem(ItemIdentifiers.RANGER_GLOVES, 50, -1, false),
                new CollectionItem(ItemIdentifiers.HOLY_WRAPS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_DRAGON_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_DRAGON_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_DRAGON_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.MITHRIL_DRAGON_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ADAMANT_DRAGON_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_DRAGON_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARCEUUS_SCARF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.HOSIDIUS_SCARF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.LOVAKENGJ_SCARF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.PISCARILIUS_SCARF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SHAYZIEN_SCARF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.KATANA, 50, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_CANE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.BUCKET_HELM, 65, -1, false),
                new CollectionItem(ItemIdentifiers.BLACKSMITHS_HELM, 65, -1, false),
                new CollectionItem(ItemIdentifiers.DEERSTALKER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.AFRO, 65, -1, false),
                new CollectionItem(ItemIdentifiers.BIG_PIRATE_HAT, 65, -1, false),
                new CollectionItem(ItemIdentifiers.TOP_HAT, 65, -1, false),
                new CollectionItem(ItemIdentifiers.MONOCLE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.BRIEFCASE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.SAGACIOUS_SPECTACLES, 65, -1, false),
                new CollectionItem(ItemIdentifiers.RANGERS_TIGHTS, 65, -1, false),
                new CollectionItem(ItemIdentifiers.URIS_HAT, 65, -1, false),
                new CollectionItem(ItemIdentifiers.GIANT_BOOT, 65, -1, false),
                new CollectionItem(ItemIdentifiers.DARK_BOW_TIE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.DARK_TUXEDO_JACKET, 65, -1, false),
                new CollectionItem(ItemIdentifiers.DARK_TUXEDO_CUFFS, 65, -1, false),
                new CollectionItem(ItemIdentifiers.DARK_TROUSERS, 65, -1, false),
                new CollectionItem(ItemIdentifiers.DARK_TUXEDO_SHOES, 65, -1, false),
                new CollectionItem(ItemIdentifiers.LIGHT_BOW_TIE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.LIGHT_TUXEDO_JACKET, 65, -1, false),
                new CollectionItem(ItemIdentifiers.LIGHT_TUXEDO_CUFFS, 65, -1, false),
                new CollectionItem(ItemIdentifiers.LIGHT_TROUSERS, 65, -1, false),
                new CollectionItem(ItemIdentifiers.LIGHT_TUXEDO_SHOES, 65, -1, false),
                new CollectionItem(ItemIdentifiers.STAMINA_POTION4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_BREW4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_RESTORE4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.EXTENDED_ANTIFIRE4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_ATTACK4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_STRENGTH4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_DEFENCE4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.RUNITE_BAR + 1, 150, 100, false),
                new CollectionItem(ItemIdentifiers.CRYSTAL_KEY + 1, 150, 10, false),
                new CollectionItem(ItemIdentifiers.COMBAT_BRACELET6, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RING_OF_WEALTH, 150, -1, false),
                new CollectionItem(ItemIdentifiers.TUNA_POTATO + 1, 150, 100, false),
                new CollectionItem(ItemIdentifiers.SUMMER_PIE + 1, 150, 50, false),
                new CollectionItem(ItemIdentifiers.RUNE_FULL_HELM, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATELEGS, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_KITESHIELD, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_LONGSWORD, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_DAGGER, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_BATTLEAXE, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_AXE, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PICKAXE, 150, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DHIDE_BODY, 150, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DHIDE_CHAPS, 150, -1, false),
                new CollectionItem(ItemIdentifiers.MAGIC_SHORTBOW, 150, -1, false),
                new CollectionItem(ItemIdentifiers.MAGIC_LONGBOW, 150, -1, false),
                new CollectionItem(ItemIdentifiers.MAGIC_COMP_BOW, 150, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_CROSSBOW, 150, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_DAGGER, 150, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_MACE, 150, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_LONGSWORD, 150, -1, false),
                new CollectionItem(ItemIdentifiers.ONYX_BOLTS, 150, 100, false),
                new CollectionItem(ItemIdentifiers.NATURE_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.DEATH_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.BLOOD_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.GRIMY_RANARR_WEED + 1, 150, 100, false),
                new CollectionItem(ItemIdentifiers.RAW_MANTA_RAY+ 1, 150, 100, false),
                new CollectionItem(ItemIdentifiers.DRAGON_FULL_HELM_ORNAMENT_KIT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGON_CHAINBODY_ORNAMENT_KIT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGON_LEGSSKIRT_ORNAMENT_KIT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGON_SQ_SHIELD_ORNAMENT_KIT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGON_SCIMITAR_ORNAMENT_KIT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.FURY_ORNAMENT_KIT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.LIGHT_INFINITY_COLOUR_KIT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.DARK_INFINITY_COLOUR_KIT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.FREMENNIK_KILT, 25, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_SCIMITAR, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_BOOTS, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_COIF, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_DHIDE_VAMBRACES, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_DHIDE_BODY, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_DHIDE_CHAPS, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_PICKAXE, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_FULL_HELM, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_PLATEBODY, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_PLATELEGS, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_PLATESKIRT, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_KITESHIELD, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_MED_HELM, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_CHAINBODY, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_SQ_SHIELD, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_2H_SWORD, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_SPEAR, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_HASTA, 1, -1, true),
                new CollectionItem(ItemIdentifiers.GILDED_SPADE, 1, -1, true),
                new CollectionItem(10330, 1, -1, true),
                new CollectionItem(10332, 1, -1, true),
                new CollectionItem(10334, 1, -1, true),
                new CollectionItem(10336, 1, -1, true),
                new CollectionItem(10338, 1, -1, true),
                new CollectionItem(10340, 1, -1, true),
                new CollectionItem(10342, 1, -1, true),
                new CollectionItem(10344, 1, -1, true),
                new CollectionItem(10346, 1, -1, true),
                new CollectionItem(10348, 1, -1, true),
                new CollectionItem(10350, 1, -1, true),
                new CollectionItem(10352, 1, -1, true),
                new CollectionItem(12422, 1, -1, true),
                new CollectionItem(12424, 1, -1, true),
                new CollectionItem(12426, 1, -1, true),
                new CollectionItem(12437, 1, -1, true),
                new CollectionItem(ItemIdentifiers.RING_OF_3RD_AGE, 1, -1, true)
            };
    }

    @Override
    public String name() {
        return "Elite Casket";
    }

    @Override
    public int id() {
        return ItemIdentifiers.REWARD_CASKET_ELITE;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.ELITE_CASKET_OPENED;
    }

    @Override
    public LogType logType() {
        return null;
    }
}
