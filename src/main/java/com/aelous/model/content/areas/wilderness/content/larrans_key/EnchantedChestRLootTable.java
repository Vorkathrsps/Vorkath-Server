/*
package net.aelous.game.content.areas.wilderness.content.larrans_key;

import net.aelous.game.world.items.Item;
import net.aelous.util.CustomItemIdentifiers;
import net.aelous.util.Utils;

import java.util.Arrays;
import java.util.List;

import static net.aelous.util.CustomItemIdentifiers.*;
import static net.aelous.util.ItemIdentifiers.*;

public class EnchantedChestRLootTable {
    private static final List<Item> COMMON_TABLE_TIER_I = Arrays.asList(
        new Item(BLIGHTED_ENTANGLE_SACK, 5),
        new Item(BLIGHTED_ANCIENT_ICE_SACK, 5),
        new Item(BLIGHTED_TELEPORT_SPELL_SACK, 5),
        new Item(BLESSED_SPIRIT_SHIELD),
        //new Item(ENCHANTED_BONES)
    );

    private static final List<Item> UNCOMMON_TABLE_TIER_I = Arrays.asList(
        //new Item(BLOOD_MONEY_CASKET),
        new Item(OCCULT_NECKLACE),
        new Item(MAGES_BOOK),
        new Item(SEERS_RING),
        new Item(MASTER_WAND),
        new Item(IMBUEMENT_SCROLL),
        new Item(TOME_OF_FIRE),
       // new Item(ENCHANTED_BONES)
    );

    private static final List<Item> RARE_TABLE_TIER_I = Arrays.asList(
        //new Item(BLOOD_MONEY_CASKET),
        new Item(DOUBLE_DROPS_LAMP),
        //new Item(ENCHANTED_BONES)
    );

    private static final List<Item> EXTREMELY_RARE_TABLE_TIER_I = Arrays.asList(
       // new Item(BLOOD_MONEY_CASKET),
        new Item(DOUBLE_DROPS_LAMP),
       // new Item(ENCHANTED_BONES)
    );

    public static Item rewardTables(int key) {
        List<Item> items = null;
        if(key == ENCHANTED_KEY_I) {
            if(Utils.rollDie(25, 1)) {
                items = EXTREMELY_RARE_TABLE_TIER_I;
            } else if (Utils.rollDie(20, 1)) {
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
