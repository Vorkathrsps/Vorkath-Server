package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.items.RottenPotato;
import com.aelous.model.content.packet_actions.interactions.items.ItemActionThree;
import com.aelous.model.inter.InterfaceConstants;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

import static com.aelous.utility.ItemIdentifiers.ROTTEN_POTATO;

/**
 * @author PVE
 * @Since augustus 27, 2020
 */
public class ItemActionThreePacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        final int itemId = packet.readShortA();
        final int slot = packet.readLEShortA();
        final int interfaceId = packet.readLEShortA();

        player.debugMessage(String.format("Third item action, itemId: %d slot: %d interfaceId: %d", itemId, slot, interfaceId));

        if (slot < 0 || slot > 27) {
            return;
        }

        Item item = player.inventory().get(slot);
        if (item != null && item.getId() == itemId) {

            if (item.getId() == ROTTEN_POTATO) {
                RottenPotato.onItemOption3(player);
                return;
            }

            if (player.locked() || player.dead() || !player.inventory().hasAt(slot)) {
                return;
            }

            if (player.busy()) {
                return;
            }

            if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
                player.getBankPin().openIfNot();
                return;
            }

            if (player.askForAccountPin()) {
                player.sendAccountPinMessage();
                return;
            }

            player.afkTimer.reset();

            if (interfaceId == InterfaceConstants.INVENTORY_INTERFACE) {
                player.stopActions(false);
                player.putAttrib(AttributeKey.ITEM_SLOT, slot);
                player.putAttrib(AttributeKey.FROM_ITEM, player.inventory().get(slot));
                player.putAttrib(AttributeKey.ITEM_ID, item.getId());
                ItemActionThree.click(player, item);
                player.getInventory().refresh();
            }
        }
    }
}
