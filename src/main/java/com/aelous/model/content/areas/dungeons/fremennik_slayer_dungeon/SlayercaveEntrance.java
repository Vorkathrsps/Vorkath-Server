package com.aelous.model.content.areas.dungeons.fremennik_slayer_dungeon;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.CAVE_ENTRANCE_2123;

public class SlayercaveEntrance extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == CAVE_ENTRANCE_2123) {
                Chain.bound(null).runFn(1, () -> player.teleport(new Tile(2808, 10002)));
                return true;
            }
        }
        return false;
    }

}
