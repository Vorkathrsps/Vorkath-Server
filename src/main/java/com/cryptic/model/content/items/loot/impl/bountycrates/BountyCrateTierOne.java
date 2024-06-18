package com.cryptic.model.content.items.loot.impl.bountycrates;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.loot.CollectionItem;
import com.cryptic.model.content.items.loot.CollectionItemListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class BountyCrateTierOne implements CollectionItemListener {
    @Override
    public @NotNull CollectionItem[] rewards() {
        return new CollectionItem[]
            {
                new CollectionItem(ItemIdentifiers.ANCIENT_EMBLEM, 10, -1, true),
                new CollectionItem(ItemIdentifiers.ANCIENT_TOTEM, 10, -1, true),
                new CollectionItem(ItemIdentifiers.ANCIENT_STATUETTE, 8, -1, true),
                new CollectionItem(ItemIdentifiers.ANCIENT_MEDALLION, 8, -1, true),
                new CollectionItem(ItemIdentifiers.ANCIENT_EFFIGY, 2, -1, true),
                new CollectionItem(ItemIdentifiers.ANCIENT_RELIC, 2, -1, true),
                new CollectionItem(ItemIdentifiers.BLIGHTED_SUPER_RESTORE4 + 1, 250, 9, false),
                new CollectionItem(ItemIdentifiers.COINS_995, 250, 250_000, false),
                new CollectionItem(ItemIdentifiers.BLIGHTED_ANGLERFISH + 1, 250, 45, false),
                new CollectionItem(ItemIdentifiers.BLIGHTED_KARAMBWAN + 1, 250, 45, false),
                new CollectionItem(ItemIdentifiers.BLIGHTED_MANTA_RAY + 1, 250, 45, false),
            };
    }

    @Override
    public String name() {
        return "Bounty Crate (Tier 1)";
    }

    @Override
    public int id() {
        return ItemIdentifiers.BOUNTY_CRATE_TIER_1;
    }

    @Override
    public boolean isItem(int id) {
        return this.id() == id;
    }

    @Override
    public AttributeKey key() {
        return null;
    }

    @Override
    public LogType logType() {
        return null;
    }
}
