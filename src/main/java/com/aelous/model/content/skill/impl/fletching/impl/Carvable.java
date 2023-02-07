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
public enum Carvable implements Fletchable {

    LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.LOGS), new FletchableItem(new Item(ItemIdentifiers.ARROW_SHAFT, 15), 1, 0.3), new FletchableItem(new Item(ItemIdentifiers.SHORTBOW_U), 5, 5.0), new FletchableItem(new Item(ItemIdentifiers.LONGBOW_U), 10, 10.0), new FletchableItem(new Item(ItemIdentifiers.WOODEN_STOCK), 9, 6.0)),
    OAK_LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.OAK_LOGS), new FletchableItem(new Item(ItemIdentifiers.ARROW_SHAFT, 30), 15, 0.3), new FletchableItem(new Item(ItemIdentifiers.OAK_SHORTBOW_U), 20, 16.5), new FletchableItem(new Item(ItemIdentifiers.OAK_LONGBOW_U), 25, 25.0), new FletchableItem(new Item(ItemIdentifiers.OAK_STOCK), 24, 16.0)),
    WILLOW_LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.WILLOW_LOGS), new FletchableItem(new Item(ItemIdentifiers.ARROW_SHAFT, 45), 30, 0.3), new FletchableItem(new Item(ItemIdentifiers.WILLOW_SHORTBOW_U), 35, 33.3), new FletchableItem(new Item(ItemIdentifiers.WILLOW_LONGBOW_U), 40, 41.5), new FletchableItem(new Item(ItemIdentifiers.WILLOW_STOCK), 39, 22.0)),
    TEAK_LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.TEAK_LOGS), new FletchableItem(new Item(ItemIdentifiers.TEAK_STOCK), 46, 27.0)),
    MAPLE_LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.MAPLE_LOGS), new FletchableItem(new Item(ItemIdentifiers.ARROW_SHAFT,60), 45, 0.3), new FletchableItem(new Item(ItemIdentifiers.MAPLE_SHORTBOW_U), 50, 50.0), new FletchableItem(new Item(ItemIdentifiers.MAPLE_LONGBOW_U), 55, 51.3), new FletchableItem(new Item(ItemIdentifiers.MAPLE_STOCK), 54, 32.0)),
    MAHOGANY_LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.MAHOGANY_LOGS), new FletchableItem(new Item(ItemIdentifiers.MAHOGANY_STOCK), 61, 41.0)),
    YEW_LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.YEW_LOGS), new FletchableItem(new Item(ItemIdentifiers.ARROW_SHAFT,75), 60, 0.3), new FletchableItem(new Item(ItemIdentifiers.YEW_SHORTBOW_U), 65, 67.5), new FletchableItem(new Item(ItemIdentifiers.YEW_LONGBOW_U), 70, 75.0), new FletchableItem(new Item(ItemIdentifiers.YEW_STOCK), 69, 50.0)),
    MAGIC_LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.MAGIC_LOGS), new FletchableItem(new Item(ItemIdentifiers.ARROW_SHAFT,90), 75, 0.3), new FletchableItem(new Item(ItemIdentifiers.MAGIC_SHORTBOW_U), 80, 83.3), new FletchableItem(new Item(ItemIdentifiers.MAGIC_LONGBOW_U), 85, 91.5), new FletchableItem(new Item(ItemIdentifiers.MAGIC_STOCK), 78, 70.0)),
    REDWOOD_LOGS(new Item(ItemIdentifiers.KNIFE), new Item(ItemIdentifiers.REDWOOD_LOGS), new FletchableItem(new Item(ItemIdentifiers.ARROW_SHAFT,105), 90, 0.3)),

    OPAL_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.OPAL), new FletchableItem(new Item(ItemIdentifiers.OPAL_BOLT_TIPS, 12), 11, 1.5)),
    JADE_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.JADE), new FletchableItem(new Item(ItemIdentifiers.JADE_BOLT_TIPS, 12), 26, 2.0)),
    PEARL_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.OYSTER_PEARL), new FletchableItem(new Item(ItemIdentifiers.PEARL_BOLT_TIPS, 24), 41, 3.2)),
    PEARLS_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.OYSTER_PEARLS), new FletchableItem(new Item(ItemIdentifiers.PEARL_BOLT_TIPS, 6), 41, 3.2)),
    TOPAZ_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.RED_TOPAZ), new FletchableItem(new Item(ItemIdentifiers.TOPAZ_BOLT_TIPS, 12), 48, 3.9)),
    SAPPHIRE_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.SAPPHIRE), new FletchableItem(new Item(ItemIdentifiers.SAPPHIRE_BOLT_TIPS, 12), 56, 4.7)),
    EMERALD_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.EMERALD), new FletchableItem(new Item(ItemIdentifiers.EMERALD_BOLT_TIPS, 12), 53, 5.5)),
    RUBY_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.RUBY), new FletchableItem(new Item(ItemIdentifiers.RUBY_BOLT_TIPS, 12), 63, 6.3)),
    DIAMOND_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.DIAMOND), new FletchableItem(new Item(ItemIdentifiers.DIAMOND_BOLT_TIPS, 12), 65, 7.0)),
    DRAGONSTONE_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.DRAGONSTONE), new FletchableItem(new Item(ItemIdentifiers.DRAGONSTONE_BOLT_TIPS, 12), 71, 8.2)),
    ONYX_BOLT_TIPS(new Item(ItemIdentifiers.CHISEL), new Item(ItemIdentifiers.ONYX), new FletchableItem(new Item(ItemIdentifiers.ONYX_BOLT_TIPS, 12), 73, 9.4));

    private final Item use;
    private final Item with;
    private final FletchableItem[] items;

    Carvable(Item use, Item with, FletchableItem... items) {
        this.use = use;
        this.with = with;
        this.items = items;
    }

    public static void load() {
        for (Carvable car : values()) {
            Fletching.addFletchable(car);
        }
    }

    @Override
    public int getAnimation() {
        return switch (this) {
            case OPAL_BOLT_TIPS, PEARL_BOLT_TIPS -> 891;
            case TOPAZ_BOLT_TIPS -> 892;
            case SAPPHIRE_BOLT_TIPS -> 888;
            case EMERALD_BOLT_TIPS -> 889;
            case RUBY_BOLT_TIPS -> 887;
            case DIAMOND_BOLT_TIPS -> 890;
            case DRAGONSTONE_BOLT_TIPS -> 890;
            case ONYX_BOLT_TIPS -> 2717;
            default -> 1248;
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
    public FletchableItem[] getFletchableItems() {
        return items;
    }

    @Override
    public String getProductionMessage() {
        return null;
    }

    @Override
    public Item[] getIngediants() {
        return new Item[] { with };
    }

    @Override
    public String getName() {
        return "Carvable";
    }
}
