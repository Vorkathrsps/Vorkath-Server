package com.aelous.model.content.items;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.utility.ItemIdentifiers.LARGE_SPADE;

/**
 * Created by Bart on 11/27/2015.
 */
public class Spade extends PacketInteraction {

    private void crypt(Player player, Tile t) {
        player.message("You've broken into a crypt!");
        player.lock();
        player.runFn(1, () -> {
            player.teleport(t);
            player.unlock();
        });
    }

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
       /* if (option == 1) {
            if (item.getId() == SPADE) {
                player.animate(830);

                if (player.tile().distance(new Tile(3575, 3297)) < 4) { // Dharok
                    crypt(player, new Tile(3556, 9718, 3));
                } else if (player.tile().distance(new Tile(3557, 3298)) < 4) { // Verac
                    crypt(player, new Tile(3578, 9706, 3));
                } else if (player.tile().distance(new Tile(3565, 3289)) < 4) { // Ahrim
                    crypt(player, new Tile(3557, 9703, 3));
                } else if (player.tile().distance(new Tile(3577, 3282)) < 4) { // Guthan
                    crypt(player, new Tile(3534, 9704, 3));
                } else if (player.tile().distance(new Tile(3566, 3276)) < 4) { // Karil
                    crypt(player, new Tile(3546, 9684, 3));
                } else if (player.tile().distance(new Tile(3554, 3283)) < 4) { // Torag
                    crypt(player, new Tile(3568, 9683, 3));
                } else {
                    player.message("Nothing interesting happens.");
                }
                return true;
            }
        }*/

        if(option == 3) {
            if(item.getId() == LARGE_SPADE) {
                player.message("Due to its sheer size, you just cannot angle it right to dig with it.");
                return true;
            }
        }
        return false;
    }
}
