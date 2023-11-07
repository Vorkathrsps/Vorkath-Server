package com.cryptic.model.entity.npc.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Origin | February, 14, 2021, 12:10
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MaxHitDummyNpc extends NPC {

    public MaxHitDummyNpc(int id, Tile tile) {
        super(NpcIdentifiers.COMBAT_DUMMY, tile);
        noRetaliation = true;
    }

    @Override
    public Entity setPositionToFace(Tile positionToFace) {
        return this;
    }

    @Override
    public Entity setEntityInteraction(Entity entity) {
        return this;
    }

    @Override
    public NPC setHitpoints(int hitpoints) {
        return this;
    }

}
