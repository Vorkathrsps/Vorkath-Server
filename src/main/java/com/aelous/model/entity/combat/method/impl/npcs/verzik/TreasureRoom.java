package com.aelous.model.entity.combat.method.impl.npcs.verzik;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.TREASURE_ROOM;

public class TreasureRoom extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == TREASURE_ROOM) {
            player.teleport(3237, 4307, player.getZ());
            return true;
        }
        return false;
    }
}
