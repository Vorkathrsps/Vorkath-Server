package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.content.packet_actions.interactions.container.SecondContainerAction;
import com.cryptic.model.content.skill.impl.slayer.content.SlayerHelm;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.Color;

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

        if (id == 22333) {
            player.message(Color.BLUE.wrap("Your starter bow has " + player.<Integer>getAttribOr(AttributeKey.STARTER_BOW_CHARGES, 0) + " charges remaining."));
            return;
        }


        if (id == 22331) {
            player.message(Color.BLUE.wrap("Your starter sword has " + player.<Integer>getAttribOr(AttributeKey.STARTER_SWORD_CHARGES, 0) + " charges remaining."));
            return;
        }

        if (id == 22335) {
            player.message(Color.BLUE.wrap("Your starter staff has " + player.<Integer>getAttribOr(AttributeKey.STARTER_STAFF_CHARGES, 0) + " charges remaining."));
            return;
        }

        player.debugMessage(String.format("ItemContainerAction: second action, container: %d slot: %d id %d", interfaceId, slot, id));
        player.putAttrib(AttributeKey.ITEM_SLOT, slot);
        player.putAttrib(AttributeKey.ITEM_ID, id);

        SecondContainerAction.secondAction(player, interfaceId, slot, id);
        player.getInventory().refresh();
    }
}
