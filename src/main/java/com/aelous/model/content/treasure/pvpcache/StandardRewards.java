package com.aelous.model.content.treasure.pvpcache;

import com.aelous.model.items.Item;
import com.aelous.utility.ItemIdentifiers;

public enum StandardRewards {

    BLOOD_MONEY(100.0, new Item(ItemIdentifiers.BLOOD_MONEY, 15_000)),
    OPAL_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.OPAL_DRAGON_BOLTS_E, 1_000)),
    DRAGONSTONE_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.DRAGONSTONE_DRAGON_BOLTS_E, 1_000)),
    RUBY_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.RUBY_DRAGON_BOLTS_E, 1_000)),
    DIAMOND_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.DIAMOND_DRAGON_BOLTS_E, 1_000)),
    ONYX_DRAGON_BOLTS_E(75.0, new Item(ItemIdentifiers.ONYX_DRAGON_BOLTS_E, 1_000)),
    ROYAL_GOWN_CROWN(50.0, new Item(ItemIdentifiers.ROYAL_CROWN)),
    ROYAL_GOWN_SCEPTRE(50.0, new Item(ItemIdentifiers.ROYAL_SCEPTRE)),
    ROYAL_GOWN_TOP(50.0, new Item(ItemIdentifiers.ROYAL_GOWN_TOP)),
    ROYAL_GOWN_BOTTOM(50.0, new Item(ItemIdentifiers.ROYAL_GOWN_BOTTOM)),
    DRAGON_CROSSBOW(40.0, new Item(ItemIdentifiers.DRAGON_CROSSBOW)),
    BANDOS_GODSWORD(40.0, new Item(ItemIdentifiers.BANDOS_GODSWORD)),
    ZAMORAK_GODSWORD(95.0, new Item(ItemIdentifiers.ZAMORAK_GODSWORD)),
    SARADOMIN_GODSWORD(90.0, new Item(ItemIdentifiers.SARADOMIN_GODSWORD)),
    ICE_ARROWS(85.0, new Item(ItemIdentifiers.ICE_ARROWS, 1_000)),
    DRAGON_JAVELIN(65.0, new Item(ItemIdentifiers.DRAGON_JAVELIN, 100)),
    DRAGON_KNIFE(60.0, new Item(ItemIdentifiers.DRAGON_KNIFE, 100)),
    DRAGON_KNIFE_P_PLUS_PLUS(55.0, new Item(ItemIdentifiers.DRAGON_KNIFEP_22810, 100)),
    DRAGON_THROWNAXE(50.0, new Item(ItemIdentifiers.DRAGON_THROWNAXE, 100)),
    ABYSSAL_DAGGER_P_13271(45.0, new Item(ItemIdentifiers.ABYSSAL_DAGGER_P_13271)),
    ODIUM_WARD(40.0, new Item(ItemIdentifiers.ODIUM_WARD)),
    MALEDICTION_WARD(37.0, new Item(ItemIdentifiers.MALEDICTION_WARD)),
    ARMADYL_CROSSBOW(35.0, new Item(ItemIdentifiers.ARMADYL_CROSSBOW)),
    DRAGONFIRE_SHIELD(30.0, new Item(ItemIdentifiers.DRAGONFIRE_SHIELD)),
    ARMADYL_CHAINSKIRT(25.0, new Item(ItemIdentifiers.ARMADYL_CHAINSKIRT)),
    ARMADYL_CHESTPLATE(20.0, new Item(ItemIdentifiers.ARMADYL_CHESTPLATE)),
    BANDOS_CHESTPLATE(15.0, new Item(ItemIdentifiers.BANDOS_CHESTPLATE)),
    BANDOS_TASSETS(10.0, new Item(ItemIdentifiers.BANDOS_TASSETS)),
    ARMADYL_GODSWORD(5.0, new Item(ItemIdentifiers.ARMADYL_GODSWORD));

    public final double probability;
    public final Item reward;

    StandardRewards(double probability, Item reward) {
        this.probability = probability;
        this.reward = reward;
    }
}
