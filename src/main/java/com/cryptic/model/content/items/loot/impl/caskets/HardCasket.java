package com.cryptic.model.content.items.loot.impl.caskets;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class HardCasket implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.ENCHANTED_HAT, 120, -1, false),
                new CollectionItem(ItemIdentifiers.ENCHANTED_TOP, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_FULL_HELM_T, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY_T, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATELEGS_T, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATESKIRT_T, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_KITESHIELD_T, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_FULL_HELM_G, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY_G, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATELEGS_G, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATESKIRT_G, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_KITESHIELD_G, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_SHIELD_H1, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_SHIELD_H2, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_SHIELD_H3, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_SHIELD_H4, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_SHIELD_H5, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_HELM_H1, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_HELM_H2, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_HELM_H3, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_HELM_H4, 120, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_HELM_H5, 120, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_FULL_HELM, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_PLATEBODY, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_PLATELEGS, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_PLATESKIRT, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_KITESHIELD, 80, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_FULL_HELM, 80, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_PLATEBODY, 80, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_PLATELEGS, 80, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_KITESHIELD, 80, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_FULL_HELM, 80, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_PLATEBODY, 80, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_PLATELEGS, 80, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_KITESHIELD, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_FULL_HELM, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_PLATEBODY, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_PLATELEGS, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_PLATESKIRT, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_KITESHIELD, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_FULL_HELM, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_PLATEBODY, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_PLATELEGS, 80, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_PLATESKIRT, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_FULL_HELM, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_PLATEBODY, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_PLATELEGS, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_PLATESKIRT, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_KITESHIELD, 80, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY_H1, 75, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY_H2, 80, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY_H3, 80, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY_H4, 80, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY_H5, 75, -1, false),
                
                
                new CollectionItem(ItemIdentifiers.RED_DHIDE_BODY_G, 80, -1, false),
                new CollectionItem(ItemIdentifiers.RED_DHIDE_BODY_T, 80, -1, false),
                new CollectionItem(ItemIdentifiers.RED_DHIDE_CHAPS_G, 80, -1, false),
                new CollectionItem(ItemIdentifiers.RED_DHIDE_CHAPS_T, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_DHIDE_BODY_G, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_DHIDE_BODY_T, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_DHIDE_CHAPS_T, 80, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_DHIDE_CHAPS_G, 80, -1, false),


                //hides
                new CollectionItem(ItemIdentifiers.SARADOMIN_COIF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_DHIDE_BODY, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_CHAPS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_BRACERS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_DHIDE_BOOTS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_COIF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_DHIDE_BODY, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_CHAPS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_BRACERS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_DHIDE_BOOTS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_COIF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_DHIDE_BODY, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_CHAPS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_BRACERS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_DHIDE_BOOTS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_COIF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_DHIDE_BODY, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_CHAPS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_BRACERS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_DHIDE_BOOTS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_COIF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_DHIDE_BODY, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_CHAPS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_BRACERS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_DHIDE_BOOTS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_COIF, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_DHIDE_BODY, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_CHAPS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_BRACERS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_DHIDE_BOOTS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_DHIDE_SHIELD, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_DHIDE_SHIELD, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_DHIDE_SHIELD, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_DHIDE_SHIELD, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_DHIDE_SHIELD, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_DHIDE_SHIELD, 50, -1, false),
                
                
                new CollectionItem(ItemIdentifiers.SARADOMIN_STOLE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_CROZIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_STOLE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_CROZIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_STOLE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_CROZIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.ZOMBIE_HEAD, 65, -1, false),
                new CollectionItem(ItemIdentifiers.PIRATES_HAT, 65, -1, false),
                new CollectionItem(ItemIdentifiers.RED_CAVALIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.WHITE_CAVALIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.NAVY_CAVALIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.TAN_CAVALIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.DARK_CAVALIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_CAVALIER, 65, -1, false),
                new CollectionItem(ItemIdentifiers.PITH_HELMET, 65, -1, false),
                new CollectionItem(ItemIdentifiers.EXPLORER_BACKPACK, 65, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_DRAGON_MASK, 65, -1, false),
                new CollectionItem(ItemIdentifiers.RED_DRAGON_MASK, 65, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_DRAGON_MASK, 65, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DRAGON_MASK, 65, -1, false),
                new CollectionItem(ItemIdentifiers.NUNCHAKU, 65, -1, false),
                new CollectionItem(ItemIdentifiers.RUNE_CANE, 65, -1, false),
                new CollectionItem(ItemIdentifiers.DUAL_SAI, 65, -1, false),
                new CollectionItem(ItemIdentifiers.THIEVING_BAG, 65, -1, false),


                //potions
                new CollectionItem(ItemIdentifiers.SUPER_ENERGY4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_RESTORE4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.ANTIFIRE_POTION4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_ATTACK4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_STRENGTH4 + 1, 90, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_DEFENCE4 + 1, 90, 50, false),

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
                new CollectionItem(ItemIdentifiers.NATURE_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.LAW_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.BLOOD_RUNE, 150, 100, false),
                new CollectionItem(ItemIdentifiers.LOBSTER + 1, 150, 100, false),
                new CollectionItem(ItemIdentifiers.SHARK + 1, 150, 100, false),

                new CollectionItem(ItemIdentifiers.RUNE_DEFENDER_ORNAMENT_KIT, 35, -1, true),
                new CollectionItem(ItemIdentifiers.BERSERKER_NECKLACE_ORNAMENT_KIT, 35, -1, true),
                new CollectionItem(ItemIdentifiers.TZHAARKETOM_ORNAMENT_KIT, 35, -1, true),
                new CollectionItem(ItemIdentifiers.AMULET_OF_GLORY_T4, 35, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGON_BOOTS_ORNAMENT_KIT, 35, -1, true),

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

                //third-age
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
                new CollectionItem(10352, 1, -1, false),
                new CollectionItem(ItemIdentifiers.ROBIN_HOOD_HAT, 10, -1, true),
            };
    }

    @Override
    public String name() {
        return "Hard Casket";
    }

    @Override
    public int id() {
        return ItemIdentifiers.REWARD_CASKET_HARD;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.HARD_CASKET_OPENED;
    }

    @Override
    public LogType logType() {
        return null;
    }
}
