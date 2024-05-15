package com.cryptic.model.content.items.loot.impl;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class LarransKey implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.BOW_OF_FAERDHINEN, 2, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_HELM, 5, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_BODY, 5, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_LEGS, 5, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_OF_AMLODD, 6, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_OF_CADARN, 6, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_OF_CRWYS, 6, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_OF_IORWERTH, 6, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_OF_ITHELL, 6, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_OF_TRAHAEARN, 6, -1, true),
                new CollectionItem(ItemIdentifiers.ECLIPSE_MOON_HELM, 8, -1, true),
                new CollectionItem(ItemIdentifiers.ECLIPSE_MOON_CHESTPLATE, 8, -1, true),
                new CollectionItem(ItemIdentifiers.ECLIPSE_MOON_TASSETS, 8, -1, true),
                new CollectionItem(ItemIdentifiers.BLOOD_MOON_HELM, 8, -1, true),
                new CollectionItem(ItemIdentifiers.BLOOD_MOON_CHESTPLATE, 8, -1, true),
                new CollectionItem(ItemIdentifiers.BLOOD_MOON_TASSETS, 8, -1, true),
                new CollectionItem(ItemIdentifiers.BLUE_MOON_HELM, 8, -1, true),
                new CollectionItem(ItemIdentifiers.BLUE_MOON_CHESTPLATE, 8, -1, true),
                new CollectionItem(ItemIdentifiers.BLUE_MOON_TASSETS, 8, -1, true),
                new CollectionItem(ItemIdentifiers.ECLIPSE_ATLATL, 8, -1, true),
                new CollectionItem(ItemIdentifiers.BLUE_MOON_SPEAR, 8, -1, true),
                new CollectionItem(ItemIdentifiers.TONALZTICS_OF_RALOS, 8, -1, true),
                new CollectionItem(ItemIdentifiers.ATLATL_DART, 15, 250, false),
                new CollectionItem(ItemIdentifiers.DAGONHAI_HAT, 12, -1, false),
                new CollectionItem(ItemIdentifiers.DAGONHAI_ROBE_TOP, 12, -1, false),
                new CollectionItem(ItemIdentifiers.DAGONHAI_ROBE_BOTTOM, 12, -1, false),
                new CollectionItem(ItemIdentifiers.UNCUT_DIAMOND + 1, 60, 100, false),
                new CollectionItem(ItemIdentifiers.UNCUT_RUBY + 1, 60, 100, false),
                new CollectionItem(ItemIdentifiers.COAL + 1, 60, 750, false),
                new CollectionItem(ItemIdentifiers.GOLD_ORE + 1, 60, 500, false),
                new CollectionItem(ItemIdentifiers.DRAGON_ARROWTIPS, 60, 350, false),
                new CollectionItem(ItemIdentifiers.COINS_995, 60, 500_000, false),
                new CollectionItem(ItemIdentifiers.IRON_ORE + 1, 60, 750, false),
                new CollectionItem(ItemIdentifiers.RUNE_FULL_HELM + 1, 60, 12, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY + 1, 60, 12, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATELEGS + 1, 60, 12, false),
                new CollectionItem(ItemIdentifiers.PURE_ESSENCE + 1, 60, 7500, false),
                new CollectionItem(ItemIdentifiers.RUNITE_ORE + 1, 60, 60, false),
                new CollectionItem(ItemIdentifiers.STEEL_BAR + 1, 60, 550, false),
                new CollectionItem(ItemIdentifiers.MAGIC_LOGS + 1, 60, 220, false),
                new CollectionItem(ItemIdentifiers.DRAGON_DART_TIP, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_TUNA + 1, 60, 500, false),
                new CollectionItem(ItemIdentifiers.RAW_LOBSTER + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_SWORDFISH + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_MONKFISH + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_SHARK + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_SEA_TURTLE + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_MANTA_RAY + 1, 60, 250, false)
            };
    }

    @Override
    public String name() {
        return "Larrans Key";
    }

    @Override
    public int id() {
        return ItemIdentifiers.LARRANS_KEY;
    }

    @Override
    public boolean isItem(int id) {
        return id == this.id();
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.LARRANS_KEYS_TIER_ONE_USED;
    }

    @Override
    public LogType logType() {
        return LogType.KEYS;
    }
}
