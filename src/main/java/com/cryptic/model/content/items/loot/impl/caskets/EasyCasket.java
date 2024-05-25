package com.cryptic.model.content.items.loot.impl.caskets;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class EasyCasket implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.WOODEN_SHIELD_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_FULL_HELM_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATELEGS_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATESKIRT_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_KITESHIELD_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_FULL_HELM_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATEBODY_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATESKIRT_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_KITESHIELD_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SHIELD_H1, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SHIELD_H2, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SHIELD_H3, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SHIELD_H4, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SHIELD_H5, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_HELM_H1, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_HELM_H2, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_HELM_H3, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_HELM_H4, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_HELM_H5, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATEBODY_H1, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATEBODY_H2, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATEBODY_H3, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATEBODY_H4, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATEBODY_H5, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_FULL_HELM_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATEBODY_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATELEGS_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATESKIRT_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_KITESHIELD_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_FULL_HELM_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATEBODY_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATELEGS_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATESKIRT_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_KITESHIELD_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_FULL_HELM_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_PLATEBODY_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_PLATELEGS_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_PLATESKIRT_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_KITESHIELD_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_FULL_HELM_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_PLATEBODY_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_PLATELEGS_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_PLATESKIRT_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_KITESHIELD_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_FULL_HELM_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_PLATEBODY_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_PLATELEGS_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_PLATESKIRT_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_KITESHIELD_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_FULL_HELM_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_PLATEBODY_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_PLATELEGS_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_PLATESKIRT_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BRONZE_KITESHIELD_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STUDDED_BODY_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STUDDED_CHAPS_G, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STUDDED_BODY_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STUDDED_CHAPS_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.LEATHER_BODY_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.LEATHER_CHAPS_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_WIZARD_HAT_G, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_WIZARD_ROBE_G, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_SKIRT_G, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_WIZARD_HAT_T, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_WIZARD_ROBE_T, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_SKIRT_T, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_WIZARD_HAT_G, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_WIZARD_ROBE_G, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SKIRT_G, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_WIZARD_HAT_T, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_WIZARD_ROBE_T, 25, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SKIRT_T, 25, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_ROBE_TOP, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_ROBE_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_ROBE_TOP, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GUTHIX_ROBE_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_ROBE_TOP, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ZAMORAK_ROBE_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_ROBE_TOP, 50, -1, false),
                new CollectionItem(ItemIdentifiers.ARMADYL_ROBE_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_ROBE_TOP, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BANDOS_ROBE_LEGS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BOBS_RED_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BOBS_GREEN_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BOBS_BLUE_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BOBS_BLACK_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BOBS_PURPLE_SHIRT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.HIGHWAYMAN_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_BERET, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_BERET, 50, -1, false),
                new CollectionItem(ItemIdentifiers.RED_BERET, 50, -1, false),
                new CollectionItem(ItemIdentifiers.WHITE_BERET, 50, -1, false),
                new CollectionItem(ItemIdentifiers.A_POWDERED_WIG, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BEANIE, 50, -1, false),
                new CollectionItem(ItemIdentifiers.IMP_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.GOBLIN_MASK, 50, -1, false),
                new CollectionItem(ItemIdentifiers.SLEEPING_CAP, 50, -1, false),
                new CollectionItem(ItemIdentifiers.FLARED_TROUSERS, 5, -1, true),
                new CollectionItem(ItemIdentifiers.PANTALOONS, 50, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_CANE, 50, -1, false),
                new CollectionItem(ItemIdentifiers.STAFF_OF_BOB_THE_CAT, 50, -1, false),
                new CollectionItem(ItemIdentifiers.AMULET_OF_POWER_T, 50, -1, false),
                new CollectionItem(ItemIdentifiers.HAM_JOINT, 5, -1, true),
                new CollectionItem(ItemIdentifiers.RAIN_BOW, 5, -1, true),
                new CollectionItem(ItemIdentifiers.COINS_995, 60, -1, false),
                new CollectionItem(ItemIdentifiers.GOLDEN_CHEFS_HAT, 60, -1, false),
                new CollectionItem(ItemIdentifiers.GOLDEN_APRON, 60, -1, false),
                new CollectionItem(ItemIdentifiers.RED_ELEGANT_SHIRT, 75, -1, false),
                new CollectionItem(ItemIdentifiers.RED_ELEGANT_BLOUSE, 75, -1, false),
                new CollectionItem(ItemIdentifiers.RED_ELEGANT_LEGS, 75, -1, false),
                new CollectionItem(ItemIdentifiers.RED_ELEGANT_SKIRT, 75, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_ELEGANT_SHIRT, 75, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_ELEGANT_BLOUSE, 75, -1, false),
                new CollectionItem(ItemIdentifiers.GREEN_ELEGANT_SKIRT, 75, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_ELEGANT_SHIRT, 75, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_ELEGANT_BLOUSE, 75, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_ELEGANT_LEGS, 75, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_ELEGANT_SKIRT, 75, -1, false),
                new CollectionItem(ItemIdentifiers.TEAM_CAPE_ZERO, 10, -1, true),
                new CollectionItem(ItemIdentifiers.TEAM_CAPE_I, 10, -1, true),
                new CollectionItem(ItemIdentifiers.TEAM_CAPE_X, 10, -1, true),
                new CollectionItem(ItemIdentifiers.CAPE_OF_SKULLS, 75, -1, false),
                new CollectionItem(ItemIdentifiers.MONKS_ROBE_TOP_G, 75, -1, false),
                new CollectionItem(ItemIdentifiers.MONKS_ROBE_G, 75, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_FULL_HELM, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATEBODY, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATELEGS, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_LONGSWORD, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_BATTLEAXE, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_AXE, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DAGGER, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PICKAXE, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PICKAXE, 90, -1, false),
                new CollectionItem(ItemIdentifiers.COIF, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STUDDED_BODY, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STUDDED_CHAPS, 90, -1, false),
                new CollectionItem(ItemIdentifiers.WILLOW_SHORTBOW, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STAFF_OF_AIR, 90, -1, false),
                new CollectionItem(ItemIdentifiers.WILLOW_LONGBOW, 90, -1, false),
                new CollectionItem(ItemIdentifiers.AMULET_OF_MAGIC, 90, -1, false),
                new CollectionItem(ItemIdentifiers.WILLOW_COMP_BOW, 90, -1, false),
                new CollectionItem(ItemIdentifiers.AMULET_OF_MAGIC_T, 90, -1, false),
                new CollectionItem(ItemIdentifiers.AIR_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.MIND_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.WATER_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.EARTH_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.FIRE_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.BODY_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.CHAOS_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.NATURE_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.LAW_RUNE, 100, 150, false),
                new CollectionItem(ItemIdentifiers.BRONZE_ARROW, 100, 100, false),
                new CollectionItem(ItemIdentifiers.IRON_ARROW, 100, 100, false),                new CollectionItem(ItemIdentifiers.LAW_RUNE, -1, -1, false),
                new CollectionItem(ItemIdentifiers.TROUT + 1, 100, 100, false),
                new CollectionItem(ItemIdentifiers.SALMON + 1, 100, 100, false),
                new CollectionItem(ItemIdentifiers.PURPLE_SWEETS, 15, 25, false),
                new CollectionItem(ItemIdentifiers.COINS_995, 100, 100_000, false),
                new CollectionItem(ItemIdentifiers.HOLY_BLESSING, 15, -1, false),
                new CollectionItem(ItemIdentifiers.UNHOLY_BLESSING, 15, -1, false),
                new CollectionItem(ItemIdentifiers.PEACEFUL_BLESSING, 15, -1, false),
                new CollectionItem(ItemIdentifiers.WAR_BLESSING, 15, -1, false),
                new CollectionItem(ItemIdentifiers.HONOURABLE_BLESSING, 15, -1, false),
                new CollectionItem(ItemIdentifiers.ANCIENT_BLESSING, 15, -1, false)
            };
    }

    @Override
    public String name() {
        return "Easy Casket";
    }

    @Override
    public int id() {
        return ItemIdentifiers.REWARD_CASKET_EASY;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.EASY_CASKET_OPENED;
    }

    @Override
    public LogType logType() {
        return null;
    }
}
