package com.cryptic.model.content.items.boxes.impl;

import com.cryptic.model.content.collection_logs.LogType;
import com.cryptic.model.content.items.boxes.MysteryBoxItem;
import com.cryptic.model.content.items.boxes.MysteryBoxListener;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.utility.ItemIdentifiers;
import org.jetbrains.annotations.NotNull;

public class LarransKey implements MysteryBoxListener {
    @Override
    public @NotNull MysteryBoxItem[] rewards() {
        return new MysteryBoxItem[]
            {
               new MysteryBoxItem(ItemIdentifiers.CRYSTAL_HELM, 5, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL_BODY, 5, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL_LEGS, 5, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL, 5, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL_OF_AMLODD, 6, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL_OF_CADARN, 2, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL_OF_CRWYS, 2, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL_OF_IORWERTH, 2, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL_OF_ITHELL, 2, -1, true),
                new MysteryBoxItem(ItemIdentifiers.CRYSTAL_OF_TRAHAEARN, 2, -1, true),
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
