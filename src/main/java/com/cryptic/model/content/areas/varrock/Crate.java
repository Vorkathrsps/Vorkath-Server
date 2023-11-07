package com.cryptic.model.content.areas.varrock;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.cache.definitions.identifiers.ObjectIdentifiers;

/**
 * @author Origin | April, 14, 2021, 19:17
 * 
 */
public class Crate extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == ObjectIdentifiers.CRATE_20885) {
                World.getWorld().shop(28).open(player);
                return true;
            }
        }
        return false;
    }
}
