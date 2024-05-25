package com.cryptic.network.packet.incoming.impl;

import com.cryptic.GameServer;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.Packet;
import com.cryptic.network.packet.PacketListener;

import java.util.concurrent.TimeUnit;

public class PlayerInactivePacketListener implements PacketListener {

    private static final boolean enabled = true;

    @Override
    public void handleMessage(Player player, Packet packet) {
        if (player.getPlayerRights().isOwner(player)) {
            return;
        }
        // ok afk timer now applies to <10min playtime accs, lets make up a verify on login
        if (player.afkTimer.elapsed(GameServer.properties().afkLogoutMinutesNewAccounts, TimeUnit.MINUTES) && player.<Long>getAttribOr(AttributeKey.GAME_TIME, 0L) < 1000L) {
            player.requestLogout();
        }

        if (player.afkTimer.elapsed(GameServer.properties().afkLogoutMinutes, TimeUnit.MINUTES) && !CombatFactory.inCombat(player)) {
            player.requestLogout();
        }
    }
}
