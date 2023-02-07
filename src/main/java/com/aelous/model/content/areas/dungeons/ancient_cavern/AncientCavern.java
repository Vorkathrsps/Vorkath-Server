package com.aelous.model.content.areas.dungeons.ancient_cavern;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

public class AncientCavern extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if (obj.getId() == WHIRLPOOL_25274) {
                player.message("You dive into the swirling maelstrom of the whirlpool.");
                player.message("You are swirled beneath the water, the darkness and pressure are overwhelming.");
                player.message("Mythical forces guide you into a cavern below the whirlpool.");
                player.teleport(1763, 5365, 1);
                return true;
            }
            if (obj.getId() == AGED_LOG) {
                player.message("You jump on the log and dislodge it. You guide your makeshift vessel through the caves to an unknown destination.");
                player.message("You find yourself on the banks of the river, far below the lake.");
                player.teleport(2531, 3445);
                return true;
            }
            if (obj.getId() == STAIRS_25336) {
                player.teleport(1768, 5366, 1);
                return true;
            }
            if (obj.getId() == STAIRS_25338) {
                player.teleport(1772, 5366, 0);
                return true;
            }
            if (obj.getId() == STAIRS_25339) {
                player.teleport(1778, 5343, 1);
                return true;
            }
            if (obj.getId() == STAIRS_25340) {
                player.teleport(1778, 5346, 0);
                return true;
            }
        }
        return false;
    }
}
