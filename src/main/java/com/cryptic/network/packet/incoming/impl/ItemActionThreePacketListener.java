package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.content.items.interactions.RottenPotato;
import com.cryptic.model.content.packet_actions.interactions.items.ItemActionThree;
import com.cryptic.model.inter.InterfaceConstants;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

import static com.cryptic.utility.ItemIdentifiers.ROTTEN_POTATO;

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
