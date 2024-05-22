package com.cryptic.model.content.items.loot.impl.caskets;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.CustomItemIdentifiers;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class MasterCasket implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.DRAGON_HALBERD, 800, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_BATTLEAXE, 800, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_SCIMITAR, 800, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_DAGGER, 800, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_MACE, 800, -1, false),
                new CollectionItem(ItemIdentifiers.DRAGON_LONGSWORD, 800, -1, false),
                new CollectionItem(ItemIdentifiers.NATURE_RUNE, 800, 100, false),
                new CollectionItem(ItemIdentifiers.SOUL_RUNE, 800, 100, false),
                new CollectionItem(ItemIdentifiers.DEATH_RUNE, 800, 100, false),
                new CollectionItem(ItemIdentifiers.BLOOD_RUNE, 800, 100, false),
                new CollectionItem(ItemIdentifiers.ONYX_BOLTS_E, 800, 100, false),
                new CollectionItem(ItemIdentifiers.MANTA_RAY+ 1, 800, 100, false),
                new CollectionItem(ItemIdentifiers.WINE_OF_ZAMORAK + 1, 800, 100, false),
                new CollectionItem(ItemIdentifiers.LIMPWURT_ROOT + 1, 800, 100, false),
                new CollectionItem(ItemIdentifiers.GRIMY_RANARR_WEED + 1, 800, 100, false),
                new CollectionItem(ItemIdentifiers.GRIMY_TOADFLAX + 1, 800, 100, false),
                new CollectionItem(ItemIdentifiers.GRIMY_SNAPDRAGON + 1, 800, 100, false),
                new CollectionItem(ItemIdentifiers.RUNITE_ORE + 1, 800, 100, false),
                new CollectionItem(ItemIdentifiers.BLACK_DRAGONHIDE + 1, 800, 100, false),
                new CollectionItem(ItemIdentifiers.CRYSTAL_KEY + 1, 100, 15, false),

                new CollectionItem(ItemIdentifiers.ARCEUUS_HOOD, 100, -1, false),
                new CollectionItem(ItemIdentifiers.HOSIDIUS_HOOD, 100, -1, false),
                new CollectionItem(ItemIdentifiers.LOVAKENGJ_HOOD, 100, -1, false),
                new CollectionItem(ItemIdentifiers.PISCARILIUS_HOOD, 100, -1, false),
                new CollectionItem(ItemIdentifiers.SHAYZIEN_HOOD, 100, -1, false),
                new CollectionItem(ItemIdentifiers.OLD_DEMON_MASK, 100, -1, false),
                new CollectionItem(ItemIdentifiers.LESSER_DEMON_MASK, 100, -1, false),
                new CollectionItem(ItemIdentifiers.GREATER_DEMON_MASK, 100, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DEMON_MASK, 100, -1, false),
                new CollectionItem(ItemIdentifiers.JUNGLE_DEMON_MASK, 100, -1, false),
                new CollectionItem(ItemIdentifiers.LEFT_EYE_PATCH, 100, -1, false),
                new CollectionItem(ItemIdentifiers.BOWL_WIG, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ALE_OF_THE_GODS, 100, -1, false),
                new CollectionItem(ItemIdentifiers.OBSIDIAN_CAPE_R, 100, -1, false),
                new CollectionItem(ItemIdentifiers.FANCY_TIARA, 100, -1, false),
                new CollectionItem(ItemIdentifiers.HALF_MOON_SPECTACLES, 100, -1, false),
                new CollectionItem(ItemIdentifiers.HOOD_OF_DARKNESS, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ROBE_TOP_OF_DARKNESS, 100, -1, false),
                new CollectionItem(ItemIdentifiers.GLOVES_OF_DARKNESS, 100, -1, false),
                new CollectionItem(ItemIdentifiers.ROBE_BOTTOM_OF_DARKNESS, 100, -1, false),
                new CollectionItem(ItemIdentifiers.BOOTS_OF_DARKNESS, 100, -1, false),
                
                new CollectionItem(ItemIdentifiers.SAMURAI_KASA, 15, -1, false),
                new CollectionItem(ItemIdentifiers.SAMURAI_GLOVES, 15, -1, false),
                new CollectionItem(ItemIdentifiers.SAMURAI_BOOTS, 15, -1, false),
                new CollectionItem(ItemIdentifiers.OCCULT_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.TORTURE_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.ANGUISH_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.TORMENTED_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGON_DEFENDER_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.ARMADYL_GODSWORD_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.BANDOS_GODSWORD_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.SARADOMIN_GODSWORD_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.ZAMORAK_GODSWORD_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGON_PLATEBODY_ORNAMENT_KIT, 15, -1, true),
                new CollectionItem(ItemIdentifiers.ANKOU_MASK, 15, -1, true),
                new CollectionItem(ItemIdentifiers.ANKOU_TOP, 15, -1, true),
                new CollectionItem(ItemIdentifiers.ANKOU_GLOVES, 15, -1, true),
                new CollectionItem(ItemIdentifiers.ANKOUS_LEGGINGS, 15, -1, true),
                new CollectionItem(ItemIdentifiers.ANKOU_SOCKS, 15, -1, true),
                new CollectionItem(ItemIdentifiers.MUMMYS_HEAD, 15, -1, false),
                new CollectionItem(ItemIdentifiers.MUMMYS_BODY, 15, -1, false),
                new CollectionItem(ItemIdentifiers.MUMMYS_HANDS, 15, -1, false),
                new CollectionItem(ItemIdentifiers.MUMMYS_LEGS, 15, -1, false),
                new CollectionItem(ItemIdentifiers.MUMMYS_FEET, 15, -1, false),
                
                new CollectionItem(ItemIdentifiers.DRAGON_KITESHIELD_ORNAMENT_KIT, 15, -1, true),
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
                new CollectionItem(103100, 1, -1, true),
                new CollectionItem(10352, 1, -1, true),
                new CollectionItem(12422, 1, -1, true),
                new CollectionItem(12424, 1, -1, true),
                new CollectionItem(12426, 1, -1, true),
                new CollectionItem(12437, 1, -1, true),

                new CollectionItem(20011, 1, -1, true),
                new CollectionItem(20014, 1, -1, true),
                new CollectionItem(23242, 1, -1, true),

                new CollectionItem(23336, 1, -1, true),
                new CollectionItem(23339, 1, -1, true),
                new CollectionItem(23342, 1, -1, true),
                new CollectionItem(23345, 1, -1, true),
                new CollectionItem(ItemIdentifiers.RING_OF_3RD_AGE, 1, -1, true),
                new CollectionItem(CustomItemIdentifiers.LUCK_OF_THE_DWARVES, 1, -1, true),
            };
    }

    @Override
    public String name() {
        return "Master Casket";
    }

    @Override
    public int id() {
        return ItemIdentifiers.REWARD_CASKET_MASTER;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.MASTER_CASKET_OPENED;
    }

    @Override
    public LogType logType() {
        return null;
    }
}
