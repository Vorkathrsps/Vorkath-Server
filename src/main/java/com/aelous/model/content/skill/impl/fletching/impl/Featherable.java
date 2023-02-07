package com.aelous.model.content.skill.impl.fletching.impl;

import com.aelous.model.content.skill.impl.fletching.Fletchable;
import com.aelous.model.content.skill.impl.fletching.FletchableItem;
import com.aelous.model.content.skill.impl.fletching.Fletching;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 17, 2020
 */
public enum Featherable implements Fletchable {

    HEADLESS_ARROWS(new Item(ItemIdentifiers.FEATHER, 15), new Item(ItemIdentifiers.ARROW_SHAFT, 15), new FletchableItem(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), 1, 1.0)),

    BRONZE_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.BRONZE_BOLTS_UNF, 10), new FletchableItem(new Item(ItemIdentifiers.BRONZE_BOLTS, 10), 9, 0.5)),
    BLURITE_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.BLURITE_BOLTS_UNF, 10), new FletchableItem(new Item(ItemIdentifiers.BLURITE_BOLTS, 10), 24, 1.0)),
    IRON_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.IRON_BOLTS_UNF, 10), new FletchableItem(new Item(ItemIdentifiers.IRON_BOLTS, 10), 39, 1.5)),
    STEEL_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.STEEL_BOLTS_UNF, 10), new FletchableItem(new Item(ItemIdentifiers.STEEL_BOLTS, 10), 46, 3.5)),
    MITHRIL_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.MITHRIL_BOLTS_UNF, 10), new FletchableItem(new Item(ItemIdentifiers.MITHRIL_BOLTS, 10), 54, 5.0)),
    BROAD_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.UNFINISHED_BROAD_BOLTS, 10), new FletchableItem(new Item(ItemIdentifiers.BROAD_BOLTS, 10), 55, 3.0)),
    ADAMANT_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.ADAMANT_BOLTSUNF, 10), new FletchableItem(new Item(ItemIdentifiers.ADAMANT_BOLTS, 10), 61, 7.0)),
    RUNITE_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.RUNITE_BOLTS_UNF, 10), new FletchableItem(new Item(ItemIdentifiers.RUNITE_BOLTS, 10), 69, 10.0)),
    DRAGON_BOLTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.DRAGON_BOLTS_UNF, 10), new FletchableItem(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), 84, 12.0)),

    BRONZE_DARTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.BRONZE_DART_TIP, 10), new FletchableItem(new Item(ItemIdentifiers.BRONZE_DART, 10), 1, 1.8)),
    IRON_DARTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.IRON_DART_TIP, 10), new FletchableItem(new Item(ItemIdentifiers.IRON_DART, 10), 22, 3.8)),
    STEEL_DARTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.STEEL_DART_TIP, 10), new FletchableItem(new Item(ItemIdentifiers.STEEL_DART, 10), 37, 7.5)),
    MITHRIL_DARTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.MITHRIL_DART_TIP, 10), new FletchableItem(new Item(ItemIdentifiers.MITHRIL_DART, 10), 52, 11.2)),
    ADAMANT_DARTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.ADAMANT_DART_TIP, 10), new FletchableItem(new Item(ItemIdentifiers.ADAMANT_DART, 10), 67, 15.0)),
    RUNE_DARTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.RUNE_DART_TIP, 10), new FletchableItem(new Item(ItemIdentifiers.RUNE_DART, 10), 81, 18.8)),
    DRAGON_DARTS(new Item(ItemIdentifiers.FEATHER, 10), new Item(ItemIdentifiers.DRAGON_DART_TIP, 10), new FletchableItem(new Item(ItemIdentifiers.DRAGON_DART, 10), 95, 25.0));

    private final Item use;
    private final Item with;
    private final FletchableItem[] items;

    Featherable(Item use, Item with, FletchableItem... items) {
        this.use = use;
        this.with = with;
        this.items = items;
    }

    public static void load() {
        for (Featherable featherable : values()) {
            Fletching.addFletchable(featherable);
        }
    }

    @Override
    public int getAnimation() {
        return 65535;
    }

    @Override
    public Item getUse() {
        return use;
    }

    @Override
    public Item getWith() {
        return with;
    }

    @Override
    public FletchableItem[] getFletchableItems() {
        return items;
    }

    @Override
    public String getProductionMessage() {
        return null;
    }

    @Override
    public String getName() {
        return "Featherable";
    }

    @Override
    public Item[] getIngediants() {
        return new Item[] { use, with };
    }
}
