package com.cryptic.model.content.packet_actions.interactions.container;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

import static com.cryptic.model.inter.InterfaceConstants.WITHDRAW_BANK;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class AllButOneAction {

    public static void allButOne(Player player, int slot, int interfaceId, int id) {
        if (interfaceId == WITHDRAW_BANK) {
            Item item = player.getBank().get(slot);
            if (item == null || item.getId() != id || item.getAmount() <= 1) {
                return;
            }
            player.getBank().withdraw(id, slot, item.getAmount() - 1);
        }
    }

}
