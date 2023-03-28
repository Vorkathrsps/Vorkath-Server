package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.content.packet_actions.interactions.container.SecondContainerAction;
import com.aelous.model.content.skill.impl.slayer.content.SlayerHelm;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.EquipSlot;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class SecondItemContainerActionPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int interfaceId = packet.readInt();
        int id = packet.readLEShortA();
        int slot = packet.readLEShort();

        if (player == null || player.dead()) {
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

        Item slayer_helm = player.getEquipment().getItems()[EquipSlot.HEAD];
        if (slayer_helm != null && slayer_helm.getId() == id) {
            if (SlayerHelm.onContainerAction2(player, slayer_helm)) {
                return;
            }
        }

        player.debugMessage(String.format("ItemContainerAction: second action, container: %d slot: %d id %d", interfaceId, slot, id));
        player.putAttrib(AttributeKey.ITEM_SLOT, slot);
        player.putAttrib(AttributeKey.ITEM_ID, id);

        SecondContainerAction.secondAction(player, interfaceId, slot, id);
    }
}
