package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.packet_actions.interactions.items.ItemOnItem;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

/**
 * @author PVE
 * @Since augustus 24, 2020
 */
public class ItemOnItemPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int toSlot = packet.readUnsignedShort();
        int fromSlot = packet.readUnsignedShortA();
        final int itemId = packet.readLEShortA();
        final int unknown = packet.readShort();
        final int unknown2 = packet.readLEShort();
        final int interfaceId = packet.readShort();

        if (fromSlot < 0 || toSlot < 0 || fromSlot > 27 || toSlot > 27) {
            return;
        }
        Item fromItem = player.inventory().get(fromSlot);
        Item toItem = player.inventory().get(toSlot);

        if (fromItem == null || toItem == null) // Avoid null items
            return;

        if (!player.locked() && !player.dead()) {
            player.stopActions(false);
            player.putAttrib(AttributeKey.ITEM_SLOT, fromSlot);
            player.putAttrib(AttributeKey.ALT_ITEM_SLOT, toSlot);
            player.putAttrib(AttributeKey.FROM_ITEM, fromItem);
            player.putAttrib(AttributeKey.TO_ITEM, toItem);
            player.putAttrib(AttributeKey.ITEM_ID, fromItem.getId());
            player.putAttrib(AttributeKey.ALT_ITEM_ID, toItem.getId());

            player.debugMessage(String.format("from=[%d] to=[%d] fromslot=[%d] toslot=[%d]", fromItem.getId(), toItem.getId(), fromSlot, toSlot));

            player.afkTimer.reset();

            // Block packet when the bank pin hasn't been entered yet
            if (!player.getBankPin().hasEnteredPin() && GameServer.properties().requireBankPinOnLogin) {
                player.getBankPin().openIfNot();
                return;
            }

            if(player.askForAccountPin()) {
                player.sendAccountPinMessage();
                return;
            }

            ItemOnItem.itemOnItem(player, fromItem, toItem);
        }
    }
}
