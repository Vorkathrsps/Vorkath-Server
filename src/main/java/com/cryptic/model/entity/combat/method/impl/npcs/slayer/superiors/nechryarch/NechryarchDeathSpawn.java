package com.cryptic.model.entity.combat.method.impl.npcs.slayer.superiors.nechryarch;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;

import java.util.List;

/**
 * @author Origin
 * april 02, 2020
 */
public class NechryarchDeathSpawn extends NPC {

    public NechryarchDeathSpawn(NPC nechryarch, Entity target, int id, Tile tile, int walkRadius) {
        super(id, tile);
        this.putAttrib(AttributeKey.BOSS_OWNER, nechryarch);
        this.setTile(tile);
        this.walkRadius(walkRadius);
        this.respawns(false);
        this.getCombat().attack(target);
    }

    public static void death(NPC npc) {
        NPC nechryarch = npc.getAttribOr(AttributeKey.BOSS_OWNER, null);
        if (nechryarch != null) {
            //Check for any minions.
            List<NPC> minList = nechryarch.getAttribOr(AttributeKey.MINION_LIST, null);
            if (minList != null) {
                minList.remove(npc);
                if (minList.size() == 0) {
                    nechryarch.putAttrib(AttributeKey.DEATH_SPAWNS_SPAWNED, false);
                }
            }
        }
    }
}
