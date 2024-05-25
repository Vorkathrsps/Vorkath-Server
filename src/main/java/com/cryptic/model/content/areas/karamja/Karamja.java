package com.cryptic.model.content.areas.karamja;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Origin
 * april 20, 2020
 */
public class Karamja extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == CLIMBING_ROPE_18969) {
                player.message("You climb up the hanging rope...");
                Chain.bound(null).runFn(2, () -> {
                    player.teleport(new Tile(2856, 3167));
                    player.message("You appear on the volcano rim.");
                });
                return true;
            }

            if(obj.getId() == ROCKS_11441) {
                player.teleport(new Tile(2856, 9567));
                player.message("You climb down through the pot hole.");
                return true;
            }

            if(obj.getId() == CAVE_ENTRANCE_11835) {
                player.teleport(new Tile(2480, 5175));
                return true;
            }
        }
        return false;
    }
}
