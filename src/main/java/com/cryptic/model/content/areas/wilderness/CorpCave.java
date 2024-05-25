package com.cryptic.model.content.areas.wilderness;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.timers.TimerKey;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.*;

public class CorpCave extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == CAVE_EXIT) {
                // from inside to outside to wildy
                player.teleport(new Tile(3206, 3681));
                return true;
            }

            if(obj.getId() == CAVE) {
                // from inside to outside to wildy
                if (obj.tile().equals(3201, 3679)) {
                    //Check to see if the player is teleblocked
                    if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                        player.teleblockMessage();
                        return true;
                    }
                    player.teleport(new Tile(2965, 4382, 2));
                }
                return true;
            }


            if(obj.getId() == PASSAGE) {
                // Actual corp entrance
                if (player.tile().x == 2970) {
                    player.teleport(player.tile().transform(4, 0, 0));
                    //TODO corp has a diff walkable interface with DMG string
                    //TODO corp beast damage attrib
                } else if (player.tile().x == 2974) {
                    player.teleport(player.tile().transform(-4, 0, 0));
                    //TODO reset walkable interface and corp dmg
                }
                return true;
            }
        }
        return false;
    }
}
