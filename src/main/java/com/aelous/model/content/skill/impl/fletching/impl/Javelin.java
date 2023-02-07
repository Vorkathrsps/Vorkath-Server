package com.aelous.model.content.skill.impl.fletching.impl;

import com.aelous.model.content.skill.impl.fletching.Fletchable;
import com.aelous.model.content.skill.impl.fletching.FletchableItem;
import com.aelous.model.content.skill.impl.fletching.Fletching;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

/**
 * @author PVE
 * @Since augustus 01, 2020
 */
public enum Javelin implements Fletchable {

    BRONZE_JAVELIN(new Item(ItemIdentifiers.JAVELIN_SHAFT, 15), new Item(ItemIdentifiers.BRONZE_JAVELIN_HEADS, 15), new FletchableItem(new Item(ItemIdentifiers.BRONZE_JAVELIN, 15), 3, 1.0)),
    IRON_JAVELIN(new Item(ItemIdentifiers.JAVELIN_SHAFT, 15), new Item(ItemIdentifiers.IRON_JAVELIN_HEADS, 15), new FletchableItem(new Item(ItemIdentifiers.IRON_JAVELIN, 15), 17, 2.0)),
    STEEL_JAVELIN(new Item(ItemIdentifiers.JAVELIN_SHAFT, 15), new Item(ItemIdentifiers.STEEL_JAVELIN_HEADS, 15), new FletchableItem(new Item(ItemIdentifiers.STEEL_JAVELIN, 15), 32, 5.0)),
    MITHRIL_JAVELIN(new Item(ItemIdentifiers.JAVELIN_SHAFT, 15), new Item(ItemIdentifiers.MITHRIL_JAVELIN_HEADS, 15), new FletchableItem(new Item(ItemIdentifiers.MITHRIL_JAVELIN, 15), 47, 8.0)),
    ADAMANT_JAVELIN(new Item(ItemIdentifiers.JAVELIN_SHAFT, 15), new Item(ItemIdentifiers.ADAMANT_JAVELIN_HEADS, 15), new FletchableItem(new Item(ItemIdentifiers.ADAMANT_JAVELIN, 15), 62, 10.0)),
    RUNITE_JAVELIN(new Item(ItemIdentifiers.JAVELIN_SHAFT, 15), new Item(ItemIdentifiers.RUNE_JAVELIN_HEADS, 15), new FletchableItem(new Item(ItemIdentifiers.RUNE_JAVELIN, 15), 77, 12.4)),
    AMETHYST_JAVELIN(new Item(ItemIdentifiers.JAVELIN_SHAFT, 15), new Item(ItemIdentifiers.AMETHYST_JAVELIN_HEADS, 15), new FletchableItem(new Item(ItemIdentifiers.AMETHYST_JAVELIN, 15), 84, 13.5)),
    DRAGON_JAVELIN(new Item(ItemIdentifiers.JAVELIN_SHAFT, 15), new Item(ItemIdentifiers.DRAGON_JAVELIN_HEADS, 15), new FletchableItem(new Item(ItemIdentifiers.DRAGON_JAVELIN, 15), 92, 15.0));

    private final Item use;
    private final Item with;
    private final FletchableItem[] items;

    Javelin(Item use, Item with, FletchableItem... items) {
        this.use = use;
        this.with = with;
        this.items = items;
    }

    public static void load() {
        for (Javelin javelin : values()) {
            Fletching.addFletchable(javelin);
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
        return "You attach javelin heads to your javelin shafts.";
    }

    @Override
    public String getName() {
        return "Javelin";
    }

    @Override
    public Item[] getIngediants() {
        return new Item[] { use, with };
    }
}
