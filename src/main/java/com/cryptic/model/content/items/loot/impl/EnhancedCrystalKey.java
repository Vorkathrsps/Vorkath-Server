package com.cryptic.model.content.items.loot.impl;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class EnhancedCrystalKey implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.CRYSTAL_PICKAXE, 2, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_AXE, 2, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_HARPOON, 2, -1, true),
                new CollectionItem(ItemIdentifiers.AMULET_OF_FURY, 5, -1, true),
                new CollectionItem(ItemIdentifiers.CRYSTAL_BOW, 10, -1, false),
                new CollectionItem(ItemIdentifiers.CRYSTAL_KEY + 1, 10, 4, false),
                new CollectionItem(ItemIdentifiers.RUNITE_BAR + 1, 25, 50, false),
                new CollectionItem(ItemIdentifiers.RUNE_FULL_HELM + 1, 25, 10, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATEBODY + 1, 25, 10, false),
                new CollectionItem(ItemIdentifiers.RUNE_PLATELEGS + 1, 25, 10, false),
                new CollectionItem(ItemIdentifiers.AMULET_OF_GLORY6 + 1, 25, 5, false),
                new CollectionItem(ItemIdentifiers.ADAMANTITE_BAR + 1, 40, 50, false),
                new CollectionItem(ItemIdentifiers.SUPER_COMBAT_POTION4 + 1, 50, 25, false),
                new CollectionItem(ItemIdentifiers.BASTION_POTION4 + 1, 50, 25, false),
                new CollectionItem(ItemIdentifiers.SARADOMIN_BREW4 + 1, 50, 25, false),
                new CollectionItem(ItemIdentifiers.SUPER_RESTORE4 + 1, 50, 25, false),
                new CollectionItem(ItemIdentifiers.PRAYER_POTION4 + 1, 50, 25, false),
                new CollectionItem(ItemIdentifiers.BLUE_DRAGONHIDE + 1, 50, 80, false),
                new CollectionItem(ItemIdentifiers.RED_DRAGONHIDE + 1, 50, 80, false),
                new CollectionItem(ItemIdentifiers.BLACK_DRAGONHIDE + 1, 50, 80, false),
                new CollectionItem(ItemIdentifiers.DRAGON_SCIMITAR + 1, 50, 5, false),
                new CollectionItem(ItemIdentifiers.BATTLESTAFF + 1, 50, 50, false),
                new CollectionItem(ItemIdentifiers.DRAGON_BONES + 1, 50, 50, false),
                new CollectionItem(ItemIdentifiers.WRATH_RUNE, 60, 1000, false),
                new CollectionItem(ItemIdentifiers.DEATH_RUNE, 60, 1000, false),
                new CollectionItem(ItemIdentifiers.BLOOD_RUNE, 60, 1000, false),
                new CollectionItem(ItemIdentifiers.NATURE_RUNE, 60, 1000, false),
                new CollectionItem(ItemIdentifiers.CHAOS_RUNE, 60, 1000, false),
                new CollectionItem(ItemIdentifiers.RAW_SHARK + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_SWORDFISH + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_ANGLERFISH + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_DARK_CRAB + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.RAW_MANTA_RAY + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.UNCUT_DIAMOND + 1, 60, 100, false),
                new CollectionItem(ItemIdentifiers.UNCUT_RUBY + 1, 60, 100, false),
                new CollectionItem(ItemIdentifiers.UNCUT_SAPPHIRE + 1, 60, 100, false),
                new CollectionItem(ItemIdentifiers.UNCUT_EMERALD + 1, 60, 100, false),
                new CollectionItem(ItemIdentifiers.UNCUT_DRAGONSTONE + 1, 60, 10, false),
                new CollectionItem(ItemIdentifiers.DRAGON_PLATELEGS + 1, 60, 5, false),
                new CollectionItem(ItemIdentifiers.DRAGON_PLATESKIRT + 1, 60, 5, false),
                new CollectionItem(ItemIdentifiers.STEEL_BAR + 1, 60, 250, false),
                new CollectionItem(ItemIdentifiers.CANNONBALL, 60, 1000, false),
            };
    }

    @Override
    public String name() {
        return "Enhanced Crystal Key";
    }

    @Override
    public int id() {
        return ItemIdentifiers.ENHANCED_CRYSTAL_KEY;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return AttributeKey.ENHANCED_CRYSTAL_KEYS_OPENED;
    }

    @Override
    public LogType logType() {
        return LogType.KEYS;
    }
}
