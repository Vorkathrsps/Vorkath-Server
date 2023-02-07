package com.aelous.model.content.mechanics.break_items;

import com.aelous.utility.ItemIdentifiers;

import java.util.HashMap;
import java.util.Map;

public enum BrokenItem {

    AMULET_OF_FURY_OR_BROKEN(ItemIdentifiers.AMULET_OF_FURY_OR, BreakItemsOnDeath.AMULET_OF_FURY_OR_BROKEN, 1000, 750),
    OCCULT_NECKLACE_OR_BROKEN(ItemIdentifiers.OCCULT_NECKLACE_OR, BreakItemsOnDeath.OCCULT_NECKLACE_OR_BROKEN, 1000, 750),
    AMULET_OF_TORTURE_OR_BROKEN(ItemIdentifiers.AMULET_OF_TORTURE_OR, BreakItemsOnDeath.AMULET_OF_TORTURE_OR_BROKEN, 6000, 2000),
    NECKLACE_OF_ANGUISH_OR_BROKEN(ItemIdentifiers.NECKLACE_OF_ANGUISH_OR, BreakItemsOnDeath.NECKLACE_OF_ANGUISH_OR_BROKEN, 5000, 2000),
    TORMENTED_BRACELET_OR_BROKEN(ItemIdentifiers.TORMENTED_BRACELET_OR, BreakItemsOnDeath.TORMENTED_BRACELET_OR_BROKEN, 4000, 2000),
    DRAGON_DEFENDER_T_BROKEN(ItemIdentifiers.DRAGON_DEFENDER_T, BreakItemsOnDeath.DRAGON_DEFENDER_T_BROKEN, 1000, 500),
    DRAGON_BOOTS_G_BROKEN(ItemIdentifiers.DRAGON_BOOTS_G, BreakItemsOnDeath.DRAGON_BOOTS_G_BROKEN, 1000, 500);

    BrokenItem(int originalItem, int brokenItem, int costToRepair, long bmDrop) {
        this.originalItem = originalItem;
        this.brokenItem = brokenItem;
        this.costToRepair = costToRepair;
        this.bmDrop = bmDrop;
    }

    public final int originalItem;
    public final int brokenItem;
    public final int costToRepair;
    public final long bmDrop;

    private static final Map<Integer, BrokenItem> brokenItems = new HashMap<>();

    public static BrokenItem get(int originalId) {
        return brokenItems.get(originalId);
    }

    static {
        for (BrokenItem brokenItem : BrokenItem.values()) {
            brokenItems.put(brokenItem.originalItem, brokenItem);
        }
    }
}
