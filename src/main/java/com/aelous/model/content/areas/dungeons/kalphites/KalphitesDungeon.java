package com.aelous.model.content.areas.dungeons.kalphites;

import com.aelous.model.content.packet_actions.interactions.objects.Ladders;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import java.util.LinkedList;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 20, 2020
 */
public class KalphitesDungeon extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == TUNNEL_23596) {
                player.message("Appearance like this tunnel is blocked.");
                return true;
            }
            if (obj.getId() == CREVICE_16465) {
                if (!player.getSkills().check(Skills.AGILITY, 86, "use this shortcut")) {
                    return true;
                }
                if (player.isAt(3500, 9510)) {
                    player.teleport(3506, 9505, 2);
                } else {
                    player.teleport(3500, 9510, 2);
                }
                return true;
            }

            if (obj.getId() == ROPE_3829) {
                Ladders.ladderUp(player, new Tile(3226, 3108, 0),true);
                return true;
            }

            if (obj.getId() == 3827) {
                Ladders.ladderUp(player, new Tile(3484, 9510, 2),true);
                return true;
            }

            //Robe up
            if(obj.getId() == ROPE_3832) {
                Ladders.ladderUp(player, new Tile(3508, 9498, 2),true);
                return true;
            }
        }

        if(option == 2) {
            if(obj.getId() == 23609) {
                player.teleport(3507, 9494, 0);
                return true;
            }

            if(obj.getId() == 29705) {
                List<Player> players = new LinkedList<>();
                World.getWorld().getPlayers().forEachInRegion(13972, players::add);

                if (players.size() == 0)
                    player.message("It doesn't look like there's anyone down there.");
                else
                    player.message("It looks like there " + (players.size() > 1 ? "are" : "is") + " " + players + " adventurer" + (players.size() > 1 ? "s" : "") + " down there.");
                return true;
            }
        }

        if(option == 3) {
            //Tunnel down
            if(obj.getId() == 23609) {
                Ladders.ladderDown(player, new Tile(3507, 9494, 0),true);
                return true;
            }
        }
        return false;
    }
}
