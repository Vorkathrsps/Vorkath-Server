package com.cryptic.model.content.areas.dungeons.fremennik_slayer_dungeon;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.CAVE_ENTRANCE_2123;

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
