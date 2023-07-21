package com.aelous.model.content.raids.theatre.boss.maiden.nylos;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;

public class MaidenNylo extends NPC {
    Tile[] spawn_tiles = new Tile[]
        {
            new Tile(0,0)
        };
    public MaidenNylo(int id, Tile tile) {
        super(id, tile);
    }

    public void walkToMaiden() {

    }

    public void healMaiden() {

    }

    @Override
    public void postSequence() {

    }

    @Override
    public void die() {
        World.getWorld().unregisterNpc(this);
    }
}
