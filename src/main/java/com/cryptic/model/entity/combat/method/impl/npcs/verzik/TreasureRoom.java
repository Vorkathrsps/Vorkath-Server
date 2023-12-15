package com.cryptic.model.entity.combat.method.impl.npcs.verzik;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.TREASURE_ROOM;

public class TreasureRoom extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == TREASURE_ROOM) {
            player.teleport(3237, 4307, player.getTheatreInstance().getzLevel());
            return true;
        }
        return false;
    }
}
