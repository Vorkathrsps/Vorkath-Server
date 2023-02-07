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
public enum Stringable implements Fletchable {

    SHORTBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.SHORTBOW_U), new FletchableItem(new Item(ItemIdentifiers.SHORTBOW), 5, 5.0)),
    LONGBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.LONGBOW_U), new FletchableItem(new Item(ItemIdentifiers.LONGBOW), 10, 5.0)),
    OAK_SHORTBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.OAK_SHORTBOW_U), new FletchableItem(new Item(ItemIdentifiers.OAK_SHORTBOW), 20, 16.5)),
    OAK_LONGBOWBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.OAK_LONGBOW_U), new FletchableItem(new Item(ItemIdentifiers.OAK_LONGBOW), 25, 25.0)),
    WILLOW_SHORTBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.WILLOW_SHORTBOW_U), new FletchableItem(new Item(ItemIdentifiers.WILLOW_SHORTBOW), 35, 32.25)),
    WILLOW_LONGBOWBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.WILLOW_LONGBOW_U), new FletchableItem(new Item(ItemIdentifiers.WILLOW_LONGBOW), 40, 41.5)),
    MAPLE_SHORTBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.MAPLE_SHORTBOW_U), new FletchableItem(new Item(ItemIdentifiers.MAPLE_SHORTBOW), 50, 50.0)),
    MAPLE_LONGBOWBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.MAPLE_LONGBOW_U), new FletchableItem(new Item(ItemIdentifiers.MAPLE_LONGBOW), 55, 58.2)),
    YEW_SHORTBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.YEW_SHORTBOW_U), new FletchableItem(new Item(ItemIdentifiers.YEW_SHORTBOW), 65, 67.5)),
    YEW_LONGBOWBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.YEW_LONGBOW_U), new FletchableItem(new Item(ItemIdentifiers.YEW_LONGBOW), 70, 75.0)),
    MAGIC_SHORTBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.MAGIC_SHORTBOW_U), new FletchableItem(new Item(ItemIdentifiers.MAGIC_SHORTBOW), 80, 83.2)),
    MAGIC_LONGBOWBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.MAGIC_LONGBOW_U), new FletchableItem(new Item(ItemIdentifiers.MAGIC_LONGBOW), 85, 91.5)),
    
    BRONZE_CROSSBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.BRONZE_CROSSBOW_U), new FletchableItem(new Item(ItemIdentifiers.BRONZE_CROSSBOW), 9, 6.0)),
    BLURITE_CROSSBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.BLURITE_CROSSBOW_U), new FletchableItem(new Item(ItemIdentifiers.BLURITE_CROSSBOW), 24, 16.0)),
    IRON_CROSSBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.IRON_CROSSBOW_U), new FletchableItem(new Item(ItemIdentifiers.IRON_CROSSBOW), 39, 22.0)),
    STEEL_CROSSBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.STEEL_CROSSBOW_U), new FletchableItem(new Item(ItemIdentifiers.STEEL_CROSSBOW), 46, 27.0)),
    MITHRIL_CROSSBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.MITHRIL_CROSSBOW_U), new FletchableItem(new Item(ItemIdentifiers.MITHRIL_CROSSBOW), 54, 32.0)),
    ADAMANT_CROSSBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.ADAMANT_CROSSBOW_U), new FletchableItem(new Item(ItemIdentifiers.ADAMANT_CROSSBOW), 61, 41.0)),
    RUNITE_CROSSBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.RUNITE_CROSSBOW_U), new FletchableItem(new Item(ItemIdentifiers.RUNE_CROSSBOW), 69, 50.0)),
    DRAGON_CROSSBOW(new Item(ItemIdentifiers.BOW_STRING), new Item(ItemIdentifiers.DRAGON_CROSSBOW_U), new FletchableItem(new Item(ItemIdentifiers.DRAGON_CROSSBOW), 78, 70.0));

    private final Item use;
    private final Item with;
    private final FletchableItem[] items;

    Stringable(Item use, Item with, FletchableItem... items) {
        this.use = use;
        this.with = with;
        this.items = items;
    }

    public static void load() {
        for (Stringable featherable : values()) {
            Fletching.addFletchable(featherable);
        }
    }

    @Override
    public int getAnimation() {
        switch (this) {
            case SHORTBOW:
                return 6678;
            case LONGBOW:
                return 6684;
            case OAK_SHORTBOW:
                return 6679;
            case OAK_LONGBOWBOW:
                return 6685;
            case WILLOW_SHORTBOW:
                return 6680;
            case WILLOW_LONGBOWBOW:
                return 6686;
            case MAPLE_SHORTBOW:
                return 6681;
            case MAPLE_LONGBOWBOW:
                return 6687;
            case YEW_SHORTBOW:
                return 6682;
            case YEW_LONGBOWBOW:
                return 6688;
            case MAGIC_SHORTBOW:
                return 6683;
            case MAGIC_LONGBOWBOW:
                return 6689;
            case BRONZE_CROSSBOW:
                return 9454;
            case STEEL_CROSSBOW:
                return 9457;
            case IRON_CROSSBOW:
                return 9459;
            case MITHRIL_CROSSBOW:
                return 9461;
            case ADAMANT_CROSSBOW:
                return 9463;
            case RUNITE_CROSSBOW:
                return 9465;
            default:
                return 6678;
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
        return "Stringable";
    }
}
