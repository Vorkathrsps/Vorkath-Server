package com.cryptic.model.content.areas.dungeons;

import com.cryptic.model.content.packet_actions.interactions.objects.Ladders;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.KINGS_LADDER;

public class DagannothLair extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            //Climb the ladder down into the boss room.
            if(obj.getId() == 10230) {
                Ladders.ladderDown(player, new Tile(2900, 4449),true);
                return true;
            }

            //Climb the ladder to get out of the boss room.
            if(obj.getId() == KINGS_LADDER) {
                Ladders.ladderUp(player, new Tile(1910, 4367),true);
                return true;
            }
        }
        return false;
    }
}
