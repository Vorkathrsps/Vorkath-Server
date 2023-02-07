package com.aelous.model.content.skill.impl.crafting.impl;

import com.aelous.model.content.skill.impl.crafting.Craftable;
import com.aelous.model.content.skill.impl.crafting.CraftableItem;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.utility.ItemIdentifiers.*;
import static com.aelous.utility.ItemIdentifiers.BLACK_DHIDE_VAMBRACES;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 16, 2020
 */
public enum Hide implements Craftable {

    GREEN_DRAGONHIDE(new Item(NEEDLE), new Item(GREEN_DRAGON_LEATHER),
        new CraftableItem(new Item(GREEN_DHIDE_BODY), new Item(GREEN_DRAGON_LEATHER, 3), 63, 186.0),
        new CraftableItem(new Item(GREEN_DHIDE_VAMBRACES), new Item(GREEN_DRAGON_LEATHER, 1), 57, 62.0),
        new CraftableItem(new Item(GREEN_DHIDE_CHAPS), new Item(GREEN_DRAGON_LEATHER, 2), 60, 124.0)),
    BLUE_DRAGONHIDE(new Item(NEEDLE), new Item(BLUE_DRAGON_LEATHER),
        new CraftableItem(new Item(BLUE_DHIDE_BODY), new Item(BLUE_DRAGON_LEATHER, 3), 71, 210.0),
        new CraftableItem(new Item(BLUE_DHIDE_VAMBRACES), new Item(BLUE_DRAGON_LEATHER, 1), 66, 70.0),
        new CraftableItem(new Item(BLUE_DHIDE_CHAPS), new Item(BLUE_DRAGON_LEATHER, 2), 68, 140.0)),
    RED_DRAGONHIDE(new Item(NEEDLE), new Item(RED_DRAGON_LEATHER),
        new CraftableItem(new Item(RED_DHIDE_BODY), new Item(RED_DRAGON_LEATHER, 3), 77, 234.0),
        new CraftableItem(new Item(RED_DHIDE_VAMBRACES), new Item(RED_DRAGON_LEATHER, 1), 76, 78.0),
        new CraftableItem(new Item(RED_DHIDE_CHAPS), new Item(RED_DRAGON_LEATHER, 2), 75, 156.0)),
    BLACK_DRAGONHIDE(new Item(NEEDLE), new Item(BLACK_DRAGON_LEATHER),
        new CraftableItem(new Item(BLACK_DHIDE_BODY), new Item(BLACK_DRAGON_LEATHER, 3), 84, 258.0),
        new CraftableItem(new Item(BLACK_DHIDE_VAMBRACES), new Item(BLACK_DRAGON_LEATHER, 1), 79, 86.0),
        new CraftableItem(new Item(BLACK_DHIDE_CHAPS), new Item(BLACK_DRAGON_LEATHER, 2), 82, 172.0)),
    SNAKESIN(new Item(NEEDLE), new Item(SNAKESKIN),
        new CraftableItem(new Item(SNAKESKIN_BODY), new Item(SNAKESKIN, 15), 82, 3 + 2/3.0),
        new CraftableItem(new Item(SNAKESKIN_CHAPS), new Item(SNAKESKIN, 12), 79, 4 + 1/6.0),
        new CraftableItem(new Item(SNAKESKIN_VAMBRACES), new Item(SNAKESKIN, 8), 79, 4 + 3/8.0),
        new CraftableItem(new Item(SNAKESKIN_BANDANA), new Item(SNAKESKIN, 5), 79, 9.0),
        new CraftableItem(new Item(SNAKESKIN_BOOTS), new Item(SNAKESKIN, 6), 84, 5.0)),
    YAK_HIDE(new Item(NEEDLE), new Item(YAKHIDE),
        new CraftableItem(new Item(YAKHIDE_ARMOUR), new Item(YAKHIDE), 43, 32.0),
        new CraftableItem(new Item(YAKHIDE_ARMOUR_10824), new Item(YAKHIDE, 2), 46, 32.0)),
    HARDLEATHER_BODY(new Item(NEEDLE), new Item(HARD_LEATHER),
        new CraftableItem(new Item(ItemIdentifiers.HARDLEATHER_BODY), new Item(HARD_LEATHER), 28, 35.0));

    private final Item use;
    private final Item with;
    private final CraftableItem[] items;

    Hide(Item use, Item with, CraftableItem... items) {
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
        return new Item[] { new Item(THREAD, items[index].getRequiredItem().getAmount()), items[index].getRequiredItem()};
    }

    @Override
    public String getName() {
        return "Hide";
    }
}
