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
public enum Crossbow implements Fletchable {

    BRONZE_CROSSBOW(new Item(ItemIdentifiers.WOODEN_STOCK), new Item(ItemIdentifiers.BRONZE_LIMBS), new FletchableItem(new Item(ItemIdentifiers.BRONZE_CROSSBOW_U), 9, 12.0)),
    BLURITE_CROSSBOW(new Item(ItemIdentifiers.OAK_STOCK), new Item(ItemIdentifiers.BLURITE_LIMBS), new FletchableItem(new Item(ItemIdentifiers.BLURITE_CROSSBOW_U), 39, 44.0)),
    IRON_CROSSBOW(new Item(ItemIdentifiers.WILLOW_STOCK), new Item(ItemIdentifiers.IRON_LIMBS), new FletchableItem(new Item(ItemIdentifiers.IRON_CROSSBOW_U), 24, 44.0)),
    STEEL_CROSSBOW(new Item(ItemIdentifiers.TEAK_STOCK), new Item(ItemIdentifiers.STEEL_LIMBS), new FletchableItem(new Item(ItemIdentifiers.STEEL_CROSSBOW_U), 46, 54.0)),
    MITHRIL_CROSSBOW(new Item(ItemIdentifiers.MAPLE_STOCK), new Item(ItemIdentifiers.MITHRIL_LIMBS), new FletchableItem(new Item(ItemIdentifiers.MITHRIL_CROSSBOW_U), 54, 64.0)),
    ADAMANT_CROSSBOW(new Item(ItemIdentifiers.MAHOGANY_STOCK), new Item(ItemIdentifiers.ADAMANTITE_LIMBS), new FletchableItem(new Item(ItemIdentifiers.ADAMANT_CROSSBOW_U), 61, 82.0)),
    RUNE_CROSSBOW(new Item(ItemIdentifiers.YEW_STOCK), new Item(ItemIdentifiers.RUNITE_LIMBS), new FletchableItem(new Item(ItemIdentifiers.RUNITE_CROSSBOW_U), 69, 100.0)),
    DRAGON_CROSSBOW(new Item(ItemIdentifiers.MAGIC_STOCK), new Item(ItemIdentifiers.DRAGON_LIMBS), new FletchableItem(new Item(ItemIdentifiers.DRAGON_CROSSBOW_U), 78, 135.0));

    private final Item use;
    private final Item with;
    private final FletchableItem[] items;

    Crossbow(Item use, Item with, FletchableItem... items) {
        this.use = use;
        this.with = with;
        this.items = items;
    }

    public static void load() {
        for (Crossbow cuttable : values()) {
            Fletching.addFletchable(cuttable);
        }
    }

    @Override
    public int getAnimation() {
        switch (this) {
            case BRONZE_CROSSBOW:
                return 4436;
            case IRON_CROSSBOW:
                return 4438;
            case STEEL_CROSSBOW:
                return 4439;
            case MITHRIL_CROSSBOW:
                return 4440;
            case ADAMANT_CROSSBOW:
                return 4441;
            case RUNE_CROSSBOW:
                return 4442;
            case DRAGON_CROSSBOW:
                return 7860;
            default:
                return 4436;
        }
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
        return "Crossbow";
    }
}
