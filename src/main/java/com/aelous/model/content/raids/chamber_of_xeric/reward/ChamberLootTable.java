package com.aelous.model.content.raids.chamber_of_xeric.reward;

import com.aelous.model.World;
import com.aelous.model.entity.npc.pets.PetDefinitions;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.items.loot.LootItem;
import com.aelous.model.items.loot.LootTable;

import static com.aelous.model.content.collection_logs.CollectionLog.RAIDS_KEY;
import static com.aelous.model.content.collection_logs.LogType.BOSSES;
import static com.aelous.utility.ItemIdentifiers.*;

public class ChamberLootTable {

    public static Item rollRegular() {
        return regularTable.rollItem();
    }

    public static Item rollUnique() {
        return uniqueTable.rollItem();
    }

    public static final LootTable uniqueTable = new LootTable()
        .addTable(1,
            new LootItem(DRAGON_CLAWS, 1, 6),
            new LootItem(ARCANE_PRAYER_SCROLL, 1, 7),
            new LootItem(TWISTED_BUCKLER, 1, 8),
            new LootItem(DRAGON_HUNTER_CROSSBOW, 1, 7),
            new LootItem(DINHS_BULWARK, 1, 6),
            new LootItem(ANCESTRAL_HAT, 1, 6),
            new LootItem(KODAI_WAND, 1, 6),
            new LootItem(DEXTEROUS_PRAYER_SCROLL, 1, 7),
            new LootItem(ELDER_MAUL, 1, 4),
            new LootItem(ANCESTRAL_ROBE_TOP, 1, 5),
            new LootItem(ANCESTRAL_ROBE_BOTTOM, 1, 5),
            new LootItem(TWISTED_BOW, 1, 1),
            new LootItem(SCYTHE_OF_VITUR, 1, 1)
        );

    public static final LootTable regularTable = new LootTable()
        .addTable(1,

            new LootItem(BLOOD_MONEY, World.getWorld().random(15000, 35000), 6),
            new LootItem(DRAGON_THROWNAXE, World.getWorld().random(125, 250), 5),
            new LootItem(DRAGON_KNIFE, World.getWorld().random(125, 250), 5),
            new LootItem(TORN_PRAYER_SCROLL, 1, 3)
        );

    public static void unlockOlmlet(Player player) {
        BOSSES.log(player, RAIDS_KEY, new Item(PetDefinitions.OLMLET.item));
        //TODO
    }

}
