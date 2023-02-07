package com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

public class VorkathArea extends PacketInteraction {

    public void poke(Player player) {
        if (player.getVorkathState() == VorkathState.AWAKE) {
            return;
        }
        TaskManager.submit(new WakeUpVorkath(player, 0));
    }

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        //Vorkath ice chunks
        if (object.getId() == 31990) {
            if (player.tile().y == 4052) {
                player.getVorkathInstance().enterInstance(player);
            } else {
                player.clearInstance(); // exit
                player.teleport(2272, 4052, 0);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == 8059 && option == 1) {
            poke(player);
            return true;
        }
        return false;
    }
}
