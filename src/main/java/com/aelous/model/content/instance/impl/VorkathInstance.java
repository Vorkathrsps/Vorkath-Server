package com.aelous.model.content.instance.impl;

import com.aelous.model.content.instance.InstancedAreaManager;
import com.aelous.model.content.instance.SingleInstancedArea;
import com.aelous.model.World;
import com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath.VorkathState;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.VORKATH_8059;

/**
 * @author Patrick van Elderen | February, 11, 2021, 09:01
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class VorkathInstance {

    /**
     * The Vorkath instance
     */
    private SingleInstancedArea instance;

    /**
     * get the instance
     * @return the instance
     */
    public SingleInstancedArea getInstance() {
        return instance;
    }

    public List<NPC> npcList = new ArrayList<>();

    public VorkathInstance() {

    }

    public NPC sleepingVorkath;
    public NPC vorkath;

    public static final Tile ENTRANCE_POINT = new Tile(2272, 4054);
    public static final Area VORKATH_AREA = new Area(2260, 4054, 2286, 4077);

    public void enterInstance(Player player) {
        instance = (SingleInstancedArea) InstancedAreaManager.getSingleton().createSingleInstancedArea(player, VORKATH_AREA);
        if (player != null && instance != null) {
            npcList.clear();
            player.teleport(ENTRANCE_POINT.transform(0, 0, instance.getzLevel()));

            //Create a Vorkath instance
            sleepingVorkath = new NPC(VORKATH_8059, ENTRANCE_POINT.transform(-3, 9, instance.getzLevel()));

            NPC vorkath = sleepingVorkath;
            vorkath.getMovementQueue().setBlockMovement(true);
            World.getWorld().registerNpc(vorkath);
            npcList.add(vorkath);

            //Just to make sure when entering the area reset vorkath's state
            player.setVorkathState(VorkathState.SLEEPING);
        }
    }

    public void clear(Player player) {
        for (NPC npc : npcList) {
            World.getWorld().unregisterNpc(npc);
        }
        npcList.clear();

        //Reset state upon leaving
        player.setVorkathState(VorkathState.SLEEPING);
    }
}
