package com.aelous.model.content.skill.impl.crafting.impl;

import com.aelous.model.content.skill.impl.crafting.Craftable;
import com.aelous.model.content.skill.impl.crafting.CraftableItem;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 16, 2020
 */
public enum Leather implements Craftable {

    LEATHER_GLOVES(new Item(NEEDLE), new Item(LEATHER), new CraftableItem(new Item(ItemIdentifiers.LEATHER_GLOVES), new Item(LEATHER), 1, 13.8)),
    LEATHER_BOOTS(new Item(NEEDLE), new Item(LEATHER), new CraftableItem(new Item(ItemIdentifiers.LEATHER_BOOTS), new Item(LEATHER), 7, 16.25)),
    LEATHER_COWL(new Item(NEEDLE), new Item(LEATHER), new CraftableItem(new Item(ItemIdentifiers.LEATHER_COWL), new Item(LEATHER), 9, 18.5)),
    LEATHER_VANBRACES(new Item(NEEDLE), new Item(LEATHER), new CraftableItem(new Item(ItemIdentifiers.LEATHER_VAMBRACES), new Item(LEATHER), 11, 22.0)),
    LEATHER_BODY(new Item(NEEDLE), new Item(LEATHER), new CraftableItem(new Item(ItemIdentifiers.LEATHER_BODY), new Item(LEATHER), 14, 25.0)),
    LEATHER_CHAPS(new Item(NEEDLE), new Item(LEATHER), new CraftableItem(new Item(ItemIdentifiers.LEATHER_CHAPS), new Item(LEATHER), 18, 27.0)),
    LEATHER_COIF(new Item(NEEDLE), new Item(LEATHER), new CraftableItem(new Item(ItemIdentifiers.COIF), new Item(LEATHER), 38, 37.0));

    private final Item use;
    private final Item with;
    private final CraftableItem[] items;

    Leather(Item use, Item with, CraftableItem... items) {
        this.use = use;
        this.with = with;
        this.items = items;
    }

    @Override
    public int getAnimation() {
        return 1249;
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
    public CraftableItem[] getCraftableItems() {
        return items;
    }

    @Override
    public String getProductionMessage() {
        return null;
    }

    @Override
    public Item[] getIngredients(int index) {
        return new Item[] { new Item(THREAD, items[index].getRequiredItem().getAmount()), items[index].getRequiredItem() };
    }

    @Override
    public String getName() {
        return "Leather";
    }
}
