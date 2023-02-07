package com.aelous.model.content.areas.lumbridge;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.DARK_HOLE;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
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
