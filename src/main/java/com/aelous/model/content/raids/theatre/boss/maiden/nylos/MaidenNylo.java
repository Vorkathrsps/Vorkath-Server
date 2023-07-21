package com.aelous.model.content.raids.theatre.boss.maiden.nylos;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.area.TheatreArea;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.timers.TimerKey;
import lombok.Getter;

public class MaidenNylo extends NPC {
    @Getter public boolean exploded = false;
    private int timer = 15;
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
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    @Override
    public void postSequence() {
        if (timer > 0) {
            timer--;
            if (timer == 0) {
                this.exploded = true;
                this.die();
            }
        }
    }

    @Override
    public void die() {
        this.timer = 15;
        this.exploded = false;
        World.getWorld().unregisterNpc(this);
    }
}
