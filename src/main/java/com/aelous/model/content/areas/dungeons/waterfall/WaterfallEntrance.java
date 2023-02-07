package com.aelous.model.content.areas.dungeons.waterfall;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

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
