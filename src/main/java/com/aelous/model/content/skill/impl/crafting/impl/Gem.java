package com.aelous.model.content.skill.impl.crafting.impl;

import com.aelous.model.content.skill.impl.crafting.Craftable;
import com.aelous.model.content.skill.impl.crafting.CraftableItem;
import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;
import lombok.Getter;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 16, 2020
 */
@Getter
public enum Gem implements Craftable {

    OPAL("Opal", new Item(CHISEL), new Item(UNCUT_OPAL), new CraftableItem(new Item(ItemIdentifiers.OPAL), new Item(UNCUT_OPAL), 1, 15.0)),
    JADE("Jade", new Item(CHISEL), new Item(UNCUT_JADE), new CraftableItem(new Item(ItemIdentifiers.JADE), new Item(UNCUT_JADE), 13, 20.0)),
    RED_TOPAZ("Red topaz", new Item(CHISEL), new Item(UNCUT_RED_TOPAZ), new CraftableItem(new Item(ItemIdentifiers.RED_TOPAZ), new Item(UNCUT_RED_TOPAZ), 16, 25.0)),
    SAPPHIRE("Sapphire", new Item(CHISEL), new Item(UNCUT_SAPPHIRE), new CraftableItem(new Item(ItemIdentifiers.SAPPHIRE), new Item(UNCUT_SAPPHIRE), 20, 50.0)),
    EMERALD("Emerald", new Item(CHISEL), new Item(UNCUT_EMERALD), new CraftableItem(new Item(ItemIdentifiers.EMERALD), new Item(UNCUT_EMERALD), 27, 67.5)),
    RUBY("Ruby", new Item(CHISEL), new Item(UNCUT_RUBY), new CraftableItem(new Item(ItemIdentifiers.RUBY), new Item(UNCUT_RUBY), 34, 85.0)),
    DIAMOND("Diamond", new Item(CHISEL), new Item(UNCUT_DIAMOND), new CraftableItem(new Item(ItemIdentifiers.DIAMOND), new Item(UNCUT_DIAMOND), 43, 107.5)),
    DRAGONSTONE("Dragonstone", new Item(CHISEL), new Item(UNCUT_DRAGONSTONE), new CraftableItem(new Item(ItemIdentifiers.DRAGONSTONE), new Item(UNCUT_DRAGONSTONE), 55, 137.5)),
    ONYX("Onyx", new Item(CHISEL), new Item(UNCUT_ONYX), new CraftableItem(new Item(ItemIdentifiers.ONYX), new Item(UNCUT_ONYX), 67, 167.5)),
    ZENYTE("Zenyte", new Item(CHISEL), new Item(UNCUT_ZENYTE), new CraftableItem(new Item(ItemIdentifiers.ZENYTE), new Item(UNCUT_ZENYTE), 89, 200.0));

    private final String cutName;
    private final Item use;
    private final Item with;
    private final CraftableItem[] items;

    Gem(String cutName, Item use, Item with, CraftableItem... items) {
        this.cutName = cutName;
        this.use = use;
        this.with = with;
        this.items = items;
    }

    @Override
    public int getAnimation() {
        return switch (this) {
            case OPAL, JADE, DIAMOND -> 886;
            case RED_TOPAZ, RUBY -> 887;
            case SAPPHIRE -> 888;
            case EMERALD -> 889;
            case DRAGONSTONE -> 885;
            case ONYX -> 2717;
            case ZENYTE -> 7185;
        };
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
        return "You cut the "+cutName+".";
    }

    @Override
    public Item[] getIngredients(int index) {
        return new Item[] { with };
    }

    @Override
    public String getName() {
        return "Gem";
    }
}
