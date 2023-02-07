package com.aelous.model.content.instance.impl;

import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.instance.InstancedAreaManager;
import com.aelous.model.content.instance.SingleInstancedArea;
import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.npcs.hydra.AlchemicalHydra;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;

public class AlchemicalHydraInstance {

    /**
     * The Alchemical hydra instance
     */
    private SingleInstancedArea instance;

    /**
     * get the instance
     *
     * @return the instance
     */
    public SingleInstancedArea getInstance() {
        return instance;
    }

    public List<NPC> npcList = new ArrayList<>();

    public AlchemicalHydraInstance() {

    }

    public static final Area ALCHEMICAL_HYDRA_AREA = new Area(1356, 10257, 1377, 10278);
    public static final Tile ENTRANCE_POINT = new Tile(1356, 10258);
    public static final Tile HYDRA_SPAWN_TILE = new Tile(1364, 10265);

    public void enterInstance(Player player) {
        instance = (SingleInstancedArea) InstancedAreaManager.getSingleton().createSingleInstancedArea(player, ALCHEMICAL_HYDRA_AREA);
        if (player != null && instance != null) {
            npcList.clear();
            player.teleport(ENTRANCE_POINT.transform(0, 0, instance.getzLevel()));

            //Create a Alchemical hydra instance, if there isn't one already spawning.
            var hydra = new AlchemicalHydra(HYDRA_SPAWN_TILE.transform(0, 0, instance.getzLevel()), player);
            hydra.putAttrib(AttributeKey.MAX_DISTANCE_FROM_SPAWN,25);
            World.getWorld().registerNpc(hydra);
            npcList.add(hydra);
        }
        if(instance != null && player != null) {
            instance.setOnTeleport((p, t) -> {
                // so now we check if the target tile is inside or outside of the instance, if its out, we know we're leaving, if inside, we don't care
                if (t.getZ() != instance.getzLevel()) {
                    playerHasLeft = true;
                    player.getPacketSender().sendEffectTimer(0, EffectTimer.MONSTER_RESPAWN);
                }
            });
        }
    }

    public boolean playerHasLeft;

    public void clear() {
        for (NPC npc : npcList) {
            World.getWorld().unregisterNpc(npc);
        }
        npcList.clear();
    }

    public void death(Player killer) {
        int respawnTimer = 50;
        if (killer != null) {
            killer.getPacketSender().sendEffectTimer((int) Utils.ticksToSeconds(respawnTimer), EffectTimer.MONSTER_RESPAWN);

            Chain.bound(null).cancelWhen(() -> {
                return playerHasLeft; // cancels as expected
            }).runFn(respawnTimer, () -> {
                //Create a new Alchemical hydra instance
                if(instance == null) {
                    return;
                }
                var hydra = new AlchemicalHydra(HYDRA_SPAWN_TILE.transform(0, 0, instance.getzLevel()), killer);
                World.getWorld().registerNpc(hydra);
                npcList.add(hydra);
            });
        }
    }
}
