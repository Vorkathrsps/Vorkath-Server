package com.aelous.model.content.areas.wilderness;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.CREVICE_19043;

/**
 * @author Patrick van Elderen | January, 17, 2021, 19:16
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DeepWildDungeon extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            if (object.getId() == CREVICE_19043) {
                if(player.tile().equals(3046, 10326)) {
                    player.teleport(new Tile(3048, 10336));
                } else if(player.tile().equals(3048, 10336)) {
                    player.teleport(new Tile(3046, 10326));
                }
                return true;
            }
        }
        return false;
    }
}
