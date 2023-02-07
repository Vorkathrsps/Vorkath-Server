package com.aelous.model.content.mechanics.break_items;

import com.aelous.model.inter.dialogue.DialogueManager;
import com.aelous.model.inter.dialogue.Expression;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import static com.aelous.utility.ItemIdentifiers.BLOOD_MONEY;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.PERDU;

/**
 * This class will handle all the enchanted items that break.
 * @author Patrick van Elderen | February, 01, 2021, 14:20
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class BreakItemsOnDeath {

    public static final int RUNE_POUCH_I_BROKEN = 14500;
    public static final int AMULET_OF_FURY_OR_BROKEN = 14501;
    public static final int OCCULT_NECKLACE_OR_BROKEN = 14502;
    public static final int AMULET_OF_TORTURE_OR_BROKEN = 14503;
    public static final int NECKLACE_OF_ANGUISH_OR_BROKEN = 14504;
    public static final int TORMENTED_BRACELET_OR_BROKEN = 14505;
    public static final int DRAGON_DEFENDER_T_BROKEN = 14506;
    public static final int DRAGON_BOOTS_G_BROKEN = 14507;

    /**
     * Gets the total cost of repairing a player's broken items.
     *
     * @param player The player with broken items
     * @return The amount of BM it costs to repair your items.
     */
    public static int getRepairCost(Player player) {
        int cost = 0;
        for (BrokenItem item : BrokenItem.values()) {
            final int brokenItems = player.inventory().count(item.brokenItem);
            cost += item.costToRepair * brokenItems;
        }
        //System.out.println("total cost "+cost);
        return cost;
    }

    /**
     * Repairs all broken items for a player.
     *
     * @param player The player repairing items.
     */
    public static void repair(Player player) {
        for (BrokenItem item : BrokenItem.values()) {
            final int count = player.inventory().count(item.brokenItem);
            if (count > 0) {
                var costToRepair = getRepairCost(player);
                boolean canRepair = false;
                int bmInInventory = player.inventory().count(BLOOD_MONEY);

                if (bmInInventory > 0) {
                    if(bmInInventory >= costToRepair) {
                        canRepair = true;
                        player.inventory().remove(new Item(BLOOD_MONEY, costToRepair));
                    }
                }

                var bmInBank = player.getBank().count(BLOOD_MONEY);
                if(bmInBank >= costToRepair) {
                    canRepair = true;
                    player.getBank().remove(BLOOD_MONEY, costToRepair);
                }

                if(canRepair) {
                    player.inventory().remove(item.brokenItem, count);
                    player.inventory().add(item.originalItem, count);
                    DialogueManager.npcChat(player, Expression.DEFAULT, PERDU, "Your items have been repaired.");
                } else {
                    DialogueManager.npcChat(player, Expression.DEFAULT, PERDU, "You could not afford repairing all your items.");
                    break;
                }
            }
        }
    }
}
