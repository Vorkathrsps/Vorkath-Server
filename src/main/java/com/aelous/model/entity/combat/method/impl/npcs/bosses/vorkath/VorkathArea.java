package com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.content.instance.InstancedAreaManager;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.VORKATH_8059;

public class VorkathArea extends PacketInteraction {

    public static final Tile ENTRANCE_POINT = new Tile(2272, 4054);
    public static final Area VORKATH_AREA = new Area(2260, 4054, 2286, 4077);

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        //Vorkath ice chunks
        if (object.getId() == 31990) {
            if (player.tile().y == 4052) {
                var instance = InstancedAreaManager.getSingleton().createInstancedArea(VORKATH_AREA);
                player.teleport(ENTRANCE_POINT.transform(0, 0, instance.getzLevel()));
                player.setInstance(instance);
                instance.addPlayer(player);

                //Create a Vorkath instance
                var sleepingVorkath = new NPC(VORKATH_8059, ENTRANCE_POINT.transform(-3, 9, instance.getzLevel()));
                sleepingVorkath.getMovementQueue().setBlockMovement(true);
                World.getWorld().registerNpc(sleepingVorkath);
                instance.addNpc(sleepingVorkath);

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
            if (npc.locked() || npc.id() != VORKATH_8059)
                return true;
            TaskManager.submit(new WakeUpVorkath(player, 0));
            return true;
        }
        return false;
    }
}
