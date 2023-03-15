package com.aelous.model.entity.npc.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

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
