package com.aelous.model.entity.npc.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author Patrick van Elderen | February, 14, 2021, 12:10
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MaxHitDummyNpc extends NPC {

    public MaxHitDummyNpc(int id, Tile tile) {
        super(NpcIdentifiers.COMBAT_DUMMY, tile);
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

    @Override
    public void sequence() {
        getCombat().process();
    }

}
