package com.cryptic.model.content.areas.fossilisland;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class FossilIslandObjectInteraction extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == 30847) {
            if (option == 1) {
                if (player.tile().y == 10264) player.teleport(player.tile().transform(0, -3, 0));
                else if (player.tile().y == 10261) player.teleport(player.tile().transform(0, 3, 0));
                return true;
            }
        }
        return false;
    }
}
