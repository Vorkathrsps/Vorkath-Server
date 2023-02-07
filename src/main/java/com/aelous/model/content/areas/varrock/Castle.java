package com.aelous.model.content.areas.varrock;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.ObjectIdentifiers;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen | April, 14, 2021, 19:18
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class Castle extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == ObjectIdentifiers.STAIRCASE_11807) {
                player.lock();
                Chain.bound(player).name("CastleTask1").runFn(1, () -> {
                    player.teleport(new Tile(player.tile().x, 3476, 1));
                    player.unlock();
                });
                return true;
            }
            if (obj.getId() == ObjectIdentifiers.STAIRCASE_11799) {
                player.lock();
                Chain.bound(player).name("CastleTask2").runFn(1, () -> {
                    player.teleport(new Tile(player.tile().x, 3472, 0));
                    player.unlock();
                });
                return true;
            }
        }
        return false;
    }
}
