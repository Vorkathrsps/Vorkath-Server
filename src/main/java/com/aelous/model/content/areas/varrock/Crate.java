package com.aelous.model.content.areas.varrock;

import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.ObjectIdentifiers;

/**
 * @author Patrick van Elderen | April, 14, 2021, 19:17
 * @see <a href="https://github.com/PVE95">Github profile</a>
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
