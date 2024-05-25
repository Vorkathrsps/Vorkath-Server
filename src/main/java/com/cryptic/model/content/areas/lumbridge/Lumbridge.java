package com.cryptic.model.content.areas.lumbridge;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.DARK_HOLE;

/**
 * @author Origin
 * april 19, 2020
 */
public class Lumbridge extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == DARK_HOLE) {
                player.teleport(new Tile(3184, 9549, 0));
                return true;
            }
        }
        return false;
    }
}
