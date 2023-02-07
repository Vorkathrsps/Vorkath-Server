package com.aelous.model.content.areas.dungeons.tzhaar;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.CAVE_EXIT_11836;

public class CaveEntrance extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == CAVE_EXIT_11836) {
                player.teleport(new Tile(2862, 9572));
                return true;
            }
        }
        return false;
    }
}
