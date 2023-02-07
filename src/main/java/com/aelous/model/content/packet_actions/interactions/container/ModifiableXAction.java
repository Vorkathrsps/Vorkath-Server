package com.aelous.model.content.packet_actions.interactions.container;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

import static com.aelous.model.inter.InterfaceConstants.WITHDRAW_BANK;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class ModifiableXAction {

    public static void modifiableXAction(Player player, int slot, int interfaceId, int id, int amount) {
        if (interfaceId == WITHDRAW_BANK) {
            final Item item = player.getBank().get(slot);

            if (item == null || item.getId() != id) {
                return;
            }

            player.getBank().withdraw(id, slot, amount);
        }
    }
}
