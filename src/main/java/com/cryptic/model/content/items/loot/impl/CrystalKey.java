package com.cryptic.model.content.items.loot.impl;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class CrystalKey implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.UNCUT_ONYX, 2, -1, true),
                new CollectionItem(ItemIdentifiers.ENHANCED_CRYSTAL_KEY, 5, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGONSTONE_FULL_HELM, 5, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGONSTONE_PLATEBODY, 5, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGONSTONE_PLATELEGS, 5, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGONSTONE_BOOTS, 5, -1, true),
                new CollectionItem(ItemIdentifiers.DRAGONSTONE_GAUNTLETS, 5, -1, true),
                new CollectionItem(ItemIdentifiers.AMULET_OF_GLORY6, 8, -1, false),
                new CollectionItem(ItemIdentifiers.ENHANCED_CRYSTAL_KEY, 10, -1, false),
                new CollectionItem(ItemIdentifiers.AIR_RUNE, 60, 1200, false),
                new CollectionItem(ItemIdentifiers.WATER_RUNE, 60, 1200, false),
                new CollectionItem(ItemIdentifiers.EARTH_RUNE, 60, 1200, false),
                new CollectionItem(ItemIdentifiers.FIRE_RUNE, 60, 1200, false),
                new CollectionItem(ItemIdentifiers.BODY_RUNE, 60, 1200, false),
                new CollectionItem(ItemIdentifiers.MIND_RUNE, 60, 1200, false),
                new CollectionItem(ItemIdentifiers.CHAOS_RUNE, 60, 800, false),
                new CollectionItem(ItemIdentifiers.DEATH_RUNE, 60, 800, false),
                new CollectionItem(ItemIdentifiers.COINS_995, 40, 500_000, false),
                new CollectionItem(ItemIdentifiers.COSMIC_RUNE, 60, 800, false),
                new CollectionItem(ItemIdentifiers.NATURE_RUNE, 60, 800, false),
                new CollectionItem(ItemIdentifiers.LAW_RUNE, 60, 800, false),
                new CollectionItem(ItemIdentifiers.UNCUT_RUBY + 1, 60, 25, false),
                new CollectionItem(ItemIdentifiers.UNCUT_DIAMOND + 1, 60, 25, false),
                new CollectionItem(ItemIdentifiers.RUNITE_BAR + 1, 60, 25, false),
                new CollectionItem(ItemIdentifiers.IRON_ORE + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.COAL + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_SWORDFISH + 1, 60, 120, false),
                new CollectionItem(ItemIdentifiers.RAW_SHARK + 1, 60, 120, false),
                new CollectionItem(ItemIdentifiers.RAW_SEA_TURTLE + 1, 60, 120, false),
                new CollectionItem(ItemIdentifiers.RAW_MANTA_RAY + 1, 60, 120, false),
                new CollectionItem(ItemIdentifiers.RAW_ANGLERFISH + 1, 60, 120, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATELEGS + 1, 60, 15, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATESKIRT + 1, 60, 15, false),
                new CollectionItem(ItemIdentifiers.DRAGONSTONE_DRAGON_BOLTS_E, 60, 100, false),
                new CollectionItem(ItemIdentifiers.DRAGON_BOLTS_UNF, 60, 100, false),
                new CollectionItem(ItemIdentifiers.DIAMOND_DRAGON_BOLTS_E, 60, 100, false),
                new CollectionItem(ItemIdentifiers.ONYX_DRAGON_BOLTS_E, 60, 100, false),
                new CollectionItem(ItemIdentifiers.OPAL_DRAGON_BOLTS_E, 60, 100, false),
                new CollectionItem(ItemIdentifiers.RUBY_DRAGON_BOLTS_E, 60, 100, false)
            };
    }

    @Override
    public String name() {
        return "Crystal Key";
    }

    @Override
    public int id() {
        return ItemIdentifiers.CRYSTAL_KEY;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.CRYSTAL_KEYS_OPENED;
    }

    @Override
    public LogType logType() {
        return LogType.KEYS;
    }
}
