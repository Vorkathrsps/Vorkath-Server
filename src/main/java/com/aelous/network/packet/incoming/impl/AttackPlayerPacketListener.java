package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

import java.lang.ref.WeakReference;

/**
 * @author PVE
 * @Since augustus 26, 2020
 */
public class AttackPlayerPacketListener implements PacketListener {

    @Override
    public void handleMessage(Player player, Packet packet) {
        player.afkTimer.reset();

        if (player.busy()) {
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

        player.stopActions(false);
        int index = packet.readLEShort();
        if (index > World.getWorld().getPlayers().capacity() || index < 0)
            return;

        if(player.locked() || player.dead()) {
            return;
        }

        final Player attacked = World.getWorld().getPlayers().get(index);

        if (attacked == null || attacked.dead() || attacked.equals(player)) {
            player.getMovementQueue().clear();
            return;
        }

        player.putAttrib(AttributeKey.INTERACTION_OPTION, 2);
        player.putAttrib(AttributeKey.TARGET, new WeakReference<Entity>(attacked));
        player.getCombat().setCastSpell(null);
        player.getCombat().attack(attacked);

    }
}
