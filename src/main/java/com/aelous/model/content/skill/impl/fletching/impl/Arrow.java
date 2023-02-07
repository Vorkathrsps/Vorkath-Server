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
public enum Arrow implements Fletchable {

    BRONZE_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.BRONZE_ARROWTIPS, 15), new FletchableItem(new Item(ItemIdentifiers.BRONZE_ARROW, 15), 1, 1.3)),
    IRON_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.IRON_ARROWTIPS, 15), new FletchableItem(new Item(ItemIdentifiers.IRON_ARROW, 15), 15, 2.5)),
    STEEL_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.STEEL_ARROWTIPS, 15), new FletchableItem(new Item(ItemIdentifiers.STEEL_ARROW, 15), 30, 5.0)),
    MITHRIL_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.MITHRIL_ARROWTIPS, 15), new FletchableItem(new Item(ItemIdentifiers.MITHRIL_ARROW, 15), 45, 7.5)),
    BROAD_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.BROAD_ARROWHEADS, 15), new FletchableItem(new Item(ItemIdentifiers.BROAD_ARROWS_4160, 15), 52, 10.0)),
    ADAMANT_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.ADAMANT_ARROWTIPS, 15), new FletchableItem(new Item(ItemIdentifiers.ADAMANT_ARROW, 15), 60, 10)),
    RUNE_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.RUNE_ARROWTIPS, 15), new FletchableItem(new Item(ItemIdentifiers.RUNE_ARROW, 15), 75, 12.5)),
    AMETHYST_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.AMETHYST_ARROWTIPS, 15), new FletchableItem(new Item(ItemIdentifiers.AMETHYST_ARROW, 15), 75, 13.5)),
    DRAGON_ARROWS(new Item(ItemIdentifiers.HEADLESS_ARROW, 15), new Item(ItemIdentifiers.DRAGON_ARROWTIPS, 15), new FletchableItem(new Item(ItemIdentifiers.DRAGON_ARROW, 15), 90, 15.0));

    private final Item use;
    private final Item with;
    private final FletchableItem[] items;

    Arrow(Item use, Item with, FletchableItem... items) {
        this.use = use;
        this.with = with;
        this.items = items;
    }

    public static void load() {
        for (Arrow cuttable : values()) {
            Fletching.addFletchable(cuttable);
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
    public Item[] getIngediants() {
        return new Item[] { use, with };
    }

    @Override
    public String getName() {
        return "Arrow";
    }
}
