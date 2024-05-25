package com.cryptic.model.entity.combat.method.impl.npcs.bosses.kraken;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class KrakenCaveInteractions extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {

        return false;
    }
}
