package com.cryptic.model.content.items.equipment;

import com.cryptic.model.content.skill.impl.mining.Pickaxe;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

/**
 * @author Origin | March, 16, 2021, 15:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class InfernalTools {

    /**
     * Flag used to check if the item passive is active or not.
     */
    public static boolean active = false;

    public static boolean canUse(Player player, int id) {
        Item equipped = player.getEquipment().get(EquipSlot.WEAPON);
        if (equipped != null && equipped.getId() == Pickaxe.INFERNAL.id) {
            return true;
        } else {
            // Loop through the inventory to find one with charges
            for (Item item : player.inventory()) {
                int charges = player.getAttribOr(AttributeKey.INFERNAL_PICKAXE_CHARGES, 0);
                if (item != null && item.getId() == Pickaxe.INFERNAL.id && charges > 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
