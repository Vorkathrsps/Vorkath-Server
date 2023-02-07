/*
package net.aelous.game.content.areas.wilderness.content.larrans_key;

import net.aelous.game.world.items.Item;
import net.aelous.util.CustomItemIdentifiers;
import net.aelous.util.Utils;

import java.util.Arrays;
import java.util.List;

import static net.aelous.util.CustomItemIdentifiers.*;
import static net.aelous.util.ItemIdentifiers.*;

public class EnchantedChestPLootTable {
        private static final List<Item> COMMON_TABLE_TIER_I = Arrays.asList(
            new Item(ENCHANTED_KEY_I, 10),
            new Item(MYSTERY_TICKET)
        );

        private static final List<Item> UNCOMMON_TABLE_TIER_I = Arrays.asList(
            new Item(ANATHEMA_WARD)
        );

        private static final List<Item> RARE_TABLE_TIER_I = Arrays.asList(
            new Item(DERANGED_MANIFESTO),
            new Item(RING_OF_DIVINATION)
        );

        private static final List<Item> EXTREMELY_RARE_TABLE_TIER_I = Arrays.asList(
            new Item(ANATHEMATIC_STONE)
        );

        public static Item rewardTables(int key) {
            List<Item> items = null;
            if(key == ENCHANTED_KEY_II) {
                if(Utils.rollDie(20, 1)) {
                    items = EXTREMELY_RARE_TABLE_TIER_I;
                } else if (Utils.rollDie(15, 1)) {
                    items = RARE_TABLE_TIER_I;
                } else if (Utils.rollDie(5, 1)) {
                    items = UNCOMMON_TABLE_TIER_I;
                } else {
                    items = COMMON_TABLE_TIER_I;
                }
            }
            return Utils.randomElement(items);
        }
}
*/
