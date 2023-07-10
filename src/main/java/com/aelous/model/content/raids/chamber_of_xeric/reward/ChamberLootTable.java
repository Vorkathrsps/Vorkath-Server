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

    public static Item rollUltraRare() {
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
            new LootItem(KODAI_INSIGNIA, 1, 6),
            new LootItem(DEXTEROUS_PRAYER_SCROLL, 1, 7),
            new LootItem(ELDER_MAUL, 1, 4),
            new LootItem(ANCESTRAL_ROBE_TOP, 1, 5),
            new LootItem(ANCESTRAL_ROBE_BOTTOM, 1, 5),
            new LootItem(CLUE_SCROLL_ELITE, 1, 3),
            new LootItem(TWISTED_ANCESTRAL_COLOUR_KIT, 1, 2),
            new LootItem(METAMORPHIC_DUST, 1, 1),
            new LootItem(OLMLET, 1, 1),
            new LootItem(TWISTED_BOW, 1, 1)
        );


    public static final LootTable regularTable = new LootTable()
        .addTable(1,
            new LootItem(COINS_995, World.getWorld().random(50000, 500000), 6),
            new LootItem(DRAGON_THROWNAXE, World.getWorld().random(125, 250), 6),
            new LootItem(DRAGON_KNIFE, World.getWorld().random(125, 250), 6),
            new LootItem(DEATH_RUNE, World.getWorld().random(1, 3640), 5),
            new LootItem(BLOOD_RUNE, World.getWorld().random(1, 4095), 5),
            new LootItem(SOUL_RUNE, World.getWorld().random(1, 6553), 5),
            new LootItem(RUNE_ARROW, World.getWorld().random(1, 9362), 5),
            new LootItem(DRAGON_ARROW, World.getWorld().random(1, 648), 5),
            new LootItem(GRIMY_RANARR_WEED + 1, World.getWorld().random(1, 163), 5),
            new LootItem(GRIMY_TOADFLAX + 1, World.getWorld().random(1, 248), 5),
            new LootItem(GRIMY_IRIT_LEAF + 1, World.getWorld().random(1, 809), 5),
            new LootItem(GRIMY_AVANTOE + 1, World.getWorld().random(1, 404), 5),
            new LootItem(GRIMY_KWUARM + 1, World.getWorld().random(1, 338), 5),
            new LootItem(GRIMY_SNAPDRAGON + 1, World.getWorld().random(1, 97), 5),
            new LootItem(GRIMY_CADANTINE + 1, World.getWorld().random(1, 394), 5),
            new LootItem(GRIMY_LANTADYME + 1, World.getWorld().random(1, 526), 5),
            new LootItem(GRIMY_DWARF_WEED + 1, World.getWorld().random(1, 655), 5),
            new LootItem(GRIMY_TORSTOL + 1, World.getWorld().random(1, 161), 5),
            new LootItem(PURE_ESSENCE + 1, World.getWorld().random(1, 65535), 5),
            new LootItem(LIZARDMAN_FANG, World.getWorld().random(125, 4681), 5),
            new LootItem(SALTPETRE, World.getWorld().random(125, 5461), 5),
            new LootItem(TEAK_PLANK, World.getWorld().random(1, 365), 5),
            new LootItem(MAHOGANY_PLANK + 1, World.getWorld().random(1, 548), 5),
            new LootItem(DARK_RELIC, World.getWorld().random(1, 2), 5)
        );

    public static void unlockOlmlet(Player player) {
        BOSSES.log(player, RAIDS_KEY, new Item(PetDefinitions.OLMLET.item));
        //TODO
    }

}
