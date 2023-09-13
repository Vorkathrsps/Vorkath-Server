package com.cryptic.model.entity.combat.method.impl.npcs.bosses.nightmare.interactions;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class NightmareInteractions extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {

        return false;
    }
}
