package com.aelous.model.content.areas.varrock;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.ObjectIdentifiers;

/**
 * @author Patrick van Elderen | April, 14, 2021, 19:19
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Cage extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(object.getId() == ObjectIdentifiers.CAGE_20873) {
            player.message("You can't unlock the pillory, you'll let all the criminals out!");
            return true;
        }
        return false;
    }
}
