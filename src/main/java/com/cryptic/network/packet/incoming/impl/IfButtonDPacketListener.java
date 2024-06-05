package com.cryptic.network.packet.incoming.impl;

import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;
import com.cryptic.utility.CombinedComponent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IfButtonDPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int selectedSub = packet.readUnsignedShort();
        int targetSub = packet.readUnsignedShort();
        int _selectedCombinedId = packet.readInt();
        int _targetCombinedId = packet.readInt();
        CombinedComponent selectedCombinedId = new CombinedComponent(_selectedCombinedId);
        CombinedComponent targetCombinedId = new CombinedComponent(_targetCombinedId);
        int selectedObj = packet.readUnsignedShort();
        int targetObj = packet.readUnsignedShort();

        int selectedInterfaceId = selectedCombinedId.getInterfaceId();
        int selectedComponentId = selectedCombinedId.getComponentId();
        int targetInterfaceId = targetCombinedId.getInterfaceId();
        int targetComponentId = targetCombinedId.getComponentId();

        if (selectedSub == 65535) {
            selectedSub = -1;
        }

        if (targetSub == 65535) {
            targetSub = -1;
        }

        if (selectedObj == 65535) {
            selectedObj = -1;
        }

        if (targetObj == 65535) {
            targetObj = -1;
        }

        log.info("Drag button: fromComponent={}:{}, fromSlot={}, fromItem={}, toComponent={}:{}, toSlot={}, toItem={}", selectedInterfaceId, selectedComponentId, selectedSub, selectedObj, targetInterfaceId, targetComponentId, targetSub, targetObj);
        if (player.activeInterface.containsKey(selectedInterfaceId)) {
            var active = player.activeInterface.get(selectedInterfaceId);
            active.onDrag(player, _selectedCombinedId, selectedSub, selectedObj, _targetCombinedId, targetSub, targetObj);
        }
    }
}
