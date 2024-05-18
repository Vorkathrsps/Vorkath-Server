package com.cryptic.model.content.items.loot.impl.caskets;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class BeginnerCasket implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.MOLE_SLIPPERS, 1, -1, true),
                new CollectionItem(ItemIdentifiers.FROG_SLIPPERS, 1, -1, true),
                new CollectionItem(ItemIdentifiers.BEAR_FEET, 1, -1, true),
                new CollectionItem(ItemIdentifiers.DEMON_FEET, 1, -1, true),
                new CollectionItem(ItemIdentifiers.JESTER_CAPE, 1, -1, true),
                new CollectionItem(ItemIdentifiers.SHOULDER_PARROT, 1, -1, true),
                new CollectionItem(ItemIdentifiers.MONKS_ROBE_TOP_T, 1, -1, true),
                new CollectionItem(ItemIdentifiers.MONKS_ROBE_T, 1, -1, true),
                new CollectionItem(ItemIdentifiers.AMULET_OF_DEFENCE_T, 1, -1, true),
                new CollectionItem(ItemIdentifiers.SANDWICH_LADY_HAT, 1, -1, true),
                new CollectionItem(ItemIdentifiers.SANDWICH_LADY_TOP, 1, -1, true),
                new CollectionItem(ItemIdentifiers.SANDWICH_LADY_BOTTOM, 1, -1, true),
                new CollectionItem(ItemIdentifiers.RUNE_SCIMITAR_ORNAMENT_KIT_GUTHIX, 1, -1, true),
                new CollectionItem(ItemIdentifiers.RUNE_SCIMITAR_ORNAMENT_KIT_SARADOMIN, 1, -1, true),
                new CollectionItem(ItemIdentifiers.RUNE_SCIMITAR_ORNAMENT_KIT_ZAMORAK, 1, -1, true),
                new CollectionItem(ItemIdentifiers.BLACK_2H_SWORD, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_AXE, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_BATTLEAXE, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_CHAINBODY, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_DAGGER, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_FULL_HELM, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_KITESHIELD, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_LONGSWORD, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_MACE, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_MED_HELM, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PICKAXE, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATEBODY, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATESKIRT, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_PLATELEGS, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SQ_SHIELD, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SCIMITAR, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_SWORD, 5, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_WARHAMMER, 5, -1, false),
                new CollectionItem(ItemIdentifiers.SHORTBOW, 60, -1, false),
                new CollectionItem(ItemIdentifiers.LONGBOW, 60, -1, false),
                new CollectionItem(ItemIdentifiers.OAK_SHORTBOW, 60, -1, false),
                new CollectionItem(ItemIdentifiers.OAK_LONGBOW, 60, -1, false),
                new CollectionItem(ItemIdentifiers.IRON_PICKAXE, 60, -1, false),
                new CollectionItem(ItemIdentifiers.STAFF_OF_AIR, 60, -1, false),
                new CollectionItem(ItemIdentifiers.STAFF_OF_WATER, 60, -1, false),
                new CollectionItem(ItemIdentifiers.STAFF_OF_EARTH, 60, -1, false),
                new CollectionItem(ItemIdentifiers.STAFF_OF_FIRE, 60, -1, false),

                new CollectionItem(ItemIdentifiers.STEEL_FULL_HELM, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATEBODY, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATELEGS, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_PLATESKIRT, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_LONGSWORD, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_DAGGER, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_AXE, 90, -1, false),
                new CollectionItem(ItemIdentifiers.STEEL_BATTLEAXE, 90, -1, false),
                new CollectionItem(ItemIdentifiers.LEATHER_COWL, 90, -1, false),
                new CollectionItem(ItemIdentifiers.LEATHER_BODY, 90, -1, false),
                new CollectionItem(ItemIdentifiers.LEATHER_CHAPS, 90, -1, false),
                new CollectionItem(ItemIdentifiers.LEATHER_VAMBRACES, 90, -1, false),
                new CollectionItem(ItemIdentifiers.HARDLEATHER_BODY, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLUE_WIZARD_HAT, 90, -1, false),
                new CollectionItem(ItemIdentifiers.WIZARD_HAT, 90, -1, false),
                new CollectionItem(ItemIdentifiers.BLACK_ROBE, 90, -1, false),
                new CollectionItem(ItemIdentifiers.AIR_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.MIND_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.WATER_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.EARTH_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.FIRE_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.BODY_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.CHAOS_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.NATURE_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.LAW_RUNE, 100, 125, false),
                new CollectionItem(ItemIdentifiers.BRONZE_ARROW, 100, 100, false),
                new CollectionItem(ItemIdentifiers.IRON_ARROW, 100, 150, false),
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
        return "Beginner Casket";
    }

    @Override
    public int id() {
        return ItemIdentifiers.REWARD_CASKET_BEGINNER;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.BEGINNER_REWARDS_OPENED;
    }

    @Override
    public LogType logType() {
        return null;
    }
}
