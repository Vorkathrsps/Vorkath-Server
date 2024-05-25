package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;

public class IfButtonTPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        CombinedComponent targetCombinedId = new CombinedComponent(packet.readInt());
        int selectedObj = packet.readShort();
        int targetSub = packet.readShort();
        CombinedComponent selectedCombinedId = new CombinedComponent(packet.readInt());
        int selectedSub = packet.readShort();
        int targetObj = packet.readShort();

        int selectedInterfaceId = selectedCombinedId.getInterfaceId();
        int selectedComponentId = selectedCombinedId.getComponentId();

        int targetInterfaceId = targetCombinedId.getInterfaceId();
        int targetComponentId = targetCombinedId.getComponentId();

        System.out.println(STR."IfButtonT(selectedInterfaceId=\{selectedInterfaceId}, selectedComponentId=\{selectedComponentId}, selectedSub=\{selectedSub}, selectedObj=\{selectedObj}, targetInterfaceId=\{targetInterfaceId}, targetComponentId=\{targetComponentId}, targetSub=\{targetSub}, targetObj=\{targetObj})");

    }
}
