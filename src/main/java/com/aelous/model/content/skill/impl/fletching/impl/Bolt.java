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
public enum Bolt implements Fletchable {

    OPAL_TIPPED_BOLTS(new Item(ItemIdentifiers.BRONZE_BOLTS, 10), new Item(ItemIdentifiers.OPAL_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.OPAL_BOLTS, 10), 11, 1.6)),
    JADE_TIPPED_BOLTS(new Item(ItemIdentifiers.BLURITE_BOLTS, 10), new Item(ItemIdentifiers.JADE_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.JADE_BOLTS, 10), 11, 2.4)),
    PEARL_TIPPED_BOLTS(new Item(ItemIdentifiers.IRON_BOLTS, 10), new Item(ItemIdentifiers.PEARL_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.PEARL_BOLTS, 10), 41, 3.2)),
    RED_TIPPED_TOPAZ_BOLTS(new Item(ItemIdentifiers.STEEL_BOLTS, 10), new Item(ItemIdentifiers.TOPAZ_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.TOPAZ_BOLTS, 10), 48, 3.9)),
    SAPPHIRE_TIPPED_BOLTS(new Item(ItemIdentifiers.MITHRIL_BOLTS, 10), new Item(ItemIdentifiers.SAPPHIRE_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.SAPPHIRE_BOLTS, 10), 56, 4.7)),
    EMERALD_TIPPED_BOLTS(new Item(ItemIdentifiers.MITHRIL_BOLTS, 10), new Item(ItemIdentifiers.EMERALD_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.EMERALD_BOLTS, 10), 58, 5.5)),
    RUBY_TIPPED_BOLTS(new Item(ItemIdentifiers.ADAMANT_BOLTS, 10), new Item(ItemIdentifiers.RUBY_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.RUBY_BOLTS, 10), 63, 6.3)),
    DIAMOND_TIPPED_BOLTS(new Item(ItemIdentifiers.ADAMANT_BOLTS, 10), new Item(ItemIdentifiers.DIAMOND_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.DIAMOND_BOLTS, 10), 65, 7.0)),
    DRAGONSTONE_TIPPED_BOLTS(new Item(ItemIdentifiers.RUNITE_BOLTS, 10), new Item(ItemIdentifiers.DRAGONSTONE_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.DRAGONSTONE_BOLTS, 10), 71, 8.2)),
    ONYX_TIPPED_BOLTS(new Item(ItemIdentifiers.RUNITE_BOLTS, 10), new Item(ItemIdentifiers.ONYX_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.ONYX_BOLTS, 10), 73, 9.4)),
    AMETHYST_TIPPED_BROAD_BOLTS(new Item(ItemIdentifiers.BROAD_BOLTS, 10), new Item(ItemIdentifiers.AMETHYST_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.AMETHYST_BROAD_BOLTS, 10), 73, 10.6)),

    DRAGON_OPAL_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.OPAL_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.OPAL_DRAGON_BOLTS, 10), 84, 1.6)),
    DRAGON_JADE_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.JADE_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.JADE_DRAGON_BOLTS, 10), 84, 2.4)),
    DRAGON_PEARL_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.PEARL_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.PEARL_DRAGON_BOLTS, 10), 84, 3.2)),
    DRAGON_RED_TOPAZ_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.TOPAZ_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.TOPAZ_DRAGON_BOLTS, 10), 84, 3.9)),
    DRAGON_SAPPHIRE_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.SAPPHIRE_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.SAPPHIRE_DRAGON_BOLTS, 10), 84, 4.7)),
    DRAGON_EMERALD_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.EMERALD_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.EMERALD_DRAGON_BOLTS, 10), 84, 5.5)),
    DRAGON_RUBY_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.RUBY_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.RUBY_DRAGON_BOLTS, 10), 84, 6.3)),
    DRAGON_DIAMOND_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.DIAMOND_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.DIAMOND_DRAGON_BOLTS, 10), 84, 7.0)),
    DRAGON_DRAGONSTONE_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.DRAGONSTONE_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.DRAGONSTONE_DRAGON_BOLTS, 10), 84, 8.2)),
    DRAGON_ONYX_BOLTS(new Item(ItemIdentifiers.DRAGON_BOLTS, 10), new Item(ItemIdentifiers.ONYX_BOLT_TIPS, 10), new FletchableItem(new Item(ItemIdentifiers.ONYX_DRAGON_BOLTS, 10), 84, 9.4));

    private final Item use;
    private final Item with;
    private final FletchableItem[] items;

    Bolt(Item use, Item with, FletchableItem... items) {
        this.use = use;
        this.with = with;
        this.items = items;
    }

    public static void load() {
        for (Bolt cuttable : values()) {
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
        return "Bolt";
    }
}
