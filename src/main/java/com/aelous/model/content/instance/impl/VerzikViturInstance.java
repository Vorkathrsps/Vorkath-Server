package com.aelous.model.content.instance.impl;

import com.aelous.model.World;
import com.aelous.model.content.EffectTimer;
import com.aelous.model.content.instance.InstancedAreaManager;
import com.aelous.model.content.instance.SingleInstancedArea;
import com.aelous.model.content.raids.RaidsNpc;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.method.impl.npcs.verzik.VerzikVitur;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;

import java.util.ArrayList;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.VERZIK_VITUR_8369;

public class VerzikViturInstance {

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

    //3168, 4311, 0

    public static final Area VERZIK_AREA = new Area(3138, 4328, 3153, 4328);

    //32653, 3678, 3216

    public static final Tile ENTRANCE_POINT = new Tile(3168, 4303);

    public static final Tile VERZIK_SPAWN_TILE = new Tile(3168, 4316);

    public boolean playerHasLeft;

    public VerzikViturInstance() {

    }

    public void enterInstance(Player player) {
        instance = (SingleInstancedArea) InstancedAreaManager.getSingleton().createSingleInstancedArea(player, VERZIK_AREA);
        if (instance == null) {
            player.message("Instance unavailable");
            return;
        }
        instance.addPlayer(player);
        npcList.clear();
        player.teleport(ENTRANCE_POINT.transform(0, 0, instance.getzLevel()));

        NPC verzik = new RaidsNpc(VERZIK_VITUR_8369, new Tile(3166, 4323, instance.getzLevel()), 1, false);
         verzik.putAttrib(AttributeKey.LOCKED_FROM_MOVEMENT, true);
        verzik.spawn();
        npcList.add(verzik);
        instance.setOnTeleport((p, t) -> {
            // so now we check if the target tile is inside or outside of the instance, if its out, we know we're leaving, if inside, we don't care
            if (t.getZ() != instance.getzLevel()) {
                playerHasLeft = true;
                player.getPacketSender().sendEffectTimer(0, EffectTimer.MONSTER_RESPAWN);
            }
        });
    }

    public void clear() {
        for (NPC npc : npcList) {
            World.getWorld().unregisterNpc(npc);
        }
        npcList.clear();
    }

}
