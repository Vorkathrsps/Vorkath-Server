package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OpNpcTPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int selectedSub = packet.readShort();
        int _selectedCombinedId = packet.readInt();
        CombinedComponent selectedCombinedId = new CombinedComponent(_selectedCombinedId);
        int selectedObj = packet.readShort();
        boolean controlKey = packet.readByte() == 1;
        int index = packet.readShort();

        int selectedInterfaceId = selectedCombinedId.getInterfaceId();
        int selectedComponentId = selectedCombinedId.getComponentId();

        log.info("Target button: selectedComponent={}:{}, selectedSlot={}, controlkey={}, index={}:{}", selectedInterfaceId, selectedComponentId, selectedSub, selectedObj, controlKey, index);

        if (player.activeInterface.containsKey(selectedInterfaceId)) {
            var active = player.activeInterface.get(selectedInterfaceId);
            final NPC found = World.getWorld().getNpcs().get(index);
            active.onTargetNpc(player, _selectedCombinedId, selectedSub, selectedObj, found);
        }
    }
}
