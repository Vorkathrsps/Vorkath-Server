package com.cryptic.model.entity.npc.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

public class UndeadMaxHitDummy extends NPC {

    public UndeadMaxHitDummy(int id, Tile tile) {
        super(NpcIdentifiers.UNDEAD_COMBAT_DUMMY, tile);
        lockMoveDamageOk();
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
