package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;

public class OpLocTPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int selectedObj = packet.readShort();
        int x = packet.readShort();
        int z = packet.readShort();
        CombinedComponent selectedCombinedId = new CombinedComponent(packet.readInt());
        int id = packet.readShort();
        boolean controlKey = packet.readByte() == 1;
        int selectedSub = packet.readShort();

      /*  System.out.println(STR."OpLocT(id=\{id}, x=\{x}, z=\{z}, controlKey=\{controlKey}, selectedInterfaceId=\{selectedCombinedId.getInterfaceId()}, selectedComponentId=\{selectedCombinedId.getComponentId()}, selectedSub=\{selectedSub}, selectedObj=\{selectedObj})");
*/
    }
}
