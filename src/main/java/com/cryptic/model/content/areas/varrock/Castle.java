package com.cryptic.model.content.areas.varrock;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.cache.definitions.identifiers.ObjectIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

/**
 * @author Origin | April, 14, 2021, 19:18
 * 
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
