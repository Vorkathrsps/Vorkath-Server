package com.cryptic.model.content.areas.dungeons.waterfall;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class WaterfallEntrance extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == 2010) {//TODO get id
                player.teleport(new Tile(2575, 9861));
                return true;
            }
        }
        return false;
    }
}
