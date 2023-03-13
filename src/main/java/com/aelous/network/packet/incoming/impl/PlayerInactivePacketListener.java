package com.aelous.network.packet.incoming.impl;

import com.aelous.GameServer;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.Packet;
import com.aelous.network.packet.PacketListener;

import java.util.concurrent.TimeUnit;

public class PlayerInactivePacketListener implements PacketListener {

    private static final boolean enabled = true;

    @Override
    public void handleMessage(Player player, Packet packet) {
        // ok afk timer now applies to <10min playtime accs, lets make up a verify on login
        if(player.afkTimer.elapsed(GameServer.properties().afkLogoutMinutesNewAccounts, TimeUnit.MINUTES) && player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L) < 1000L) {
            player.requestLogout();
        }

        if (player.afkTimer.elapsed(GameServer.properties().afkLogoutMinutes, TimeUnit.MINUTES) && !CombatFactory.inCombat(player)) {
            player.requestLogout();
        }
    }
}
