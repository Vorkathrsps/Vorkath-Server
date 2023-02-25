package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.items.RottenPotato;
import com.aelous.model.content.packet_actions.interactions.items.ItemActionOne;
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
public class ItemActionOnePacketListener  implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        final int interfaceId = packet.readShort();
        final int id = packet.readShort();
        final int slot = packet.readShort();

        player.debugMessage(String.format("First item action, interface: %d id: %d slot: %d", interfaceId, id, slot));

        if (slot < 0 || slot > 27)
            return;

        final Item item = player.inventory().get(slot);

        if (item != null && item.getId() == id) {

            if(item.getId() == ROTTEN_POTATO) {
                RottenPotato.onItemOption1(player);
                return;
            }

            if (player.locked() || player.dead()) {
                return;
            }

            if (player.busy()) {
                return;
            }

            if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
                player.getBankPin().openIfNot();
                return;
            }

            if(player.askForAccountPin()) {
                player.sendAccountPinMessage();
                return;
            }

            player.afkTimer.reset();

            if (interfaceId == InterfaceConstants.INVENTORY_INTERFACE) {
                player.stopActions(false);
                player.putAttrib(AttributeKey.ITEM_SLOT, slot);
                player.putAttrib(AttributeKey.FROM_ITEM, player.inventory().get(slot));
                player.putAttrib(AttributeKey.ITEM_ID, item.getId());
                ItemActionOne.click(player, item);
                player.getInventory().refresh();
            }
        }
    }
}
