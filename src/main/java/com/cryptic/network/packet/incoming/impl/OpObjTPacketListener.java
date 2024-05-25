package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;

public class OpObjTPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        CombinedComponent selectedCombinedId = new CombinedComponent(packet.readInt());
        int id = packet.readShort();
        int x = packet.readShort();
        int selectedSub = packet.readShort();
        int z = packet.readShort();
        int selectedObj = packet.readShort();
        boolean controlKey = packet.readByte() == 1;

        int selectedInterfaceId = selectedCombinedId.getInterfaceId();
        int selectedComponentId = selectedCombinedId.getComponentId();

        System.out.println(STR."OpObjT(id=\{id}, x=\{x}, z=\{z}, controlKey=\{controlKey}, selectedInterfaceId=\{selectedInterfaceId}, selectedComponentId=\{selectedComponentId}, selectedSub=\{selectedSub}, selectedObj=\{selectedObj})");

    }
}
