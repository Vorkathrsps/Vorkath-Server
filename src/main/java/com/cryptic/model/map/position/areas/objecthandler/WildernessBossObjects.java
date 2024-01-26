package com.cryptic.model.map.position.areas.objecthandler;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Utils;

public class WildernessBossObjects extends PacketInteraction {
    final Tile[] tiles = new Tile[]{new Tile(3338, 10286), new Tile(3381, 10286), new Tile(3361, 10293)};
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        var randomTile = Utils.randomElement(tiles);
        if (obj.getId() == 47122) {
            player.teleport(randomTile);
            return true;
        }
        if (obj.getId() == 47000) {
            player.teleport(randomTile);
            return true;
        }
        if (obj.getId() == 46925) {
            player.teleport(randomTile);
            return true;
        }
        if (obj.getId() == 47149) {
            player.teleport(new Tile(3284, 3806));
            return true;
        }
        if (obj.getId() == 47147) {
            player.teleport(new Tile(3284, 3773));
            return true;
        }
        if (obj.getId() == 47077) {//TODO fall animation
            player.teleport(new Tile(1631, 11556, 2));
            return true;
        } //vetion
        if (obj.getId() == 46995) {
            player.teleport(new Tile(1887, 11552, 1));
            return true;
        }// vet
        if (obj.getId() == 47140) { //calisto
            player.teleport(new Tile(1759, 11550));
            return true;
        }
        if (obj.getId() == 40388) {
            player.teleport(new Tile(3385, 10052));
            return true;
        }
        if (obj.getId() == 40390) {
            player.teleport(new Tile(3406, 10145));
            return true;
        }
        if (obj.getId() == 40391) {
            player.teleport(new Tile(3293, 3749));
            return true;
        }
        if (obj.getId() == 40389) {
            player.teleport(new Tile(3259, 3663));
            return true;
        }
        return false;
    }

}
