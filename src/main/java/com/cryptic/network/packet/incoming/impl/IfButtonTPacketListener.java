package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IfButtonTPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int _targetCombinedId = packet.readInt();
        CombinedComponent targetCombinedId = new CombinedComponent(_targetCombinedId);
        int selectedObj = packet.readShort();
        int targetSub = packet.readShort();
        int _selectedCombinedId = packet.readInt();
        CombinedComponent selectedCombinedId = new CombinedComponent(_selectedCombinedId);
        int selectedSub = packet.readShort();
        int targetObj = packet.readShort();

        int selectedInterfaceId = selectedCombinedId.getInterfaceId();
        int selectedComponentId = selectedCombinedId.getComponentId();

        int targetInterfaceId = targetCombinedId.getInterfaceId();
        int targetComponentId = targetCombinedId.getComponentId();

        log.info("Target button: selectedComponent={}:{}, selectedSlot={}, selectedItem={}, targetComponent={}:{}, targetSlot={}, targetItem={}", selectedInterfaceId, selectedComponentId, selectedSub, selectedObj, targetInterfaceId, targetComponentId, targetSub, targetObj);
        if (player.activeInterface.containsKey(selectedInterfaceId)) {
            var active = player.activeInterface.get(selectedInterfaceId);
            active.onTargetButton(player, _selectedCombinedId, selectedSub, selectedObj, _targetCombinedId, targetSub, targetObj);
        }

    }
}
