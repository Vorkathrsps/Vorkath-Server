package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;

public class OpNpcTPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int selectedSub = packet.readShort();
        CombinedComponent selectedCombinedId = new CombinedComponent(packet.readInt());
        int selectedObj = packet.readShort();
        boolean controlKey = packet.readByte() == 1;
        int index = packet.readShort();

        int selectedInterfaceId = selectedCombinedId.getInterfaceId();
        int selectedComponentId = selectedCombinedId.getComponentId();

        /*System.out.println(STR."OpLocT(id=\{index}, controlKey=\{controlKey}, selectedInterfaceId=\{selectedInterfaceId}, selectedComponentId=\{selectedComponentId}, selectedSub=\{selectedSub}, selectedObj=\{selectedObj})");
*/
    }
}
