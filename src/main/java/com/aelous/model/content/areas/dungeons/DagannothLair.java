package com.aelous.model.content.areas.dungeons;

import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.KINGS_LADDER;
import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.KINGS_LADDER_10230;

public class DagannothLair extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            //Climb the ladder down into the boss room.
            if(obj.getId() == KINGS_LADDER_10230) {
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
