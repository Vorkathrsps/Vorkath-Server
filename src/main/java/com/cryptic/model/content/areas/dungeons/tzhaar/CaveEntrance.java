package com.cryptic.model.content.areas.dungeons.tzhaar;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.CAVE_EXIT_11836;

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
