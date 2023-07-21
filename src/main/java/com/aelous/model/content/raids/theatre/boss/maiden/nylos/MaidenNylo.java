package com.aelous.model.content.raids.theatre.boss.maiden.nylos;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;

public class MaidenNylo extends NPC {
    public static final Tile[] spawn_tiles = new Tile[]
        {
            new Tile(3186,4436),
            new Tile(3182, 4436),
            new Tile(3179, 4436),
            new Tile(3175, 4436),
            new Tile(3186, 4457),
            new Tile(3182, 4457),
            new Tile(3178, 4457),
            new Tile(3174, 4457)
        };

    public MaidenNylo(int id, Tile tile) {
        super(id, tile);
    }

    @Override
    public void die() {
        World.getWorld().unregisterNpc(this);
    }
}
