package com.aelous.network.packet.incoming.impl;

import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

/**
 * This packet listener handles the action when pressing
 * a special attack bar.
 * @author Professor Oak
 */

public class SpecialAttackPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        int specialBarButton = packet.readInt();

        if (player == null || player.dead()) {
            return;
        }
        player.afkTimer.reset();

        if(CombatSpecial.specialAttackButton(player, specialBarButton)) {
            return;
        }
        CombatSpecial.activate(player);
    }
}
