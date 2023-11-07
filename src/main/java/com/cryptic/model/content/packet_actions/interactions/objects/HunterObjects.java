package com.cryptic.model.content.packet_actions.interactions.objects;

import com.cryptic.model.content.skill.impl.hunter.Hunter;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import java.util.stream.IntStream;

/**
 * @author Origin | Zerikoth | PVE
 * @date februari 21, 2020 21:17
 */
public class HunterObjects extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        final int[] HUNTER_OBJECTS = new int[]{9344, 9345, 9373, 9377, 9379, 9375, 9380, 9348, 9385, 9382};
        if (IntStream.of(HUNTER_OBJECTS).anyMatch(id -> obj.getId() == id)) {
            if (Hunter.pickup(player, obj)) {
                return true;
            }
            if (Hunter.claim(player, obj)) {
                return true;
            }
        }
        return false;
    }
}
