package com.aelous.model.content.raids.theatre.boss.maiden.nylos;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.boss.maiden.handler.MaidenProcess;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.map.position.Tile;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class MaidenNylo extends NPC {
    @Getter
    public boolean exploded = false;
    MaidenProcess maiden;
    private int timer = 30;
    public static final Tile[] spawn_tiles = new Tile[]
        {
            new Tile(3186, 4436),
            new Tile(3182, 4436),
            new Tile(3179, 4436),
            new Tile(3175, 4436),
            new Tile(3186, 4457),
            new Tile(3182, 4457),
            new Tile(3178, 4457),
            new Tile(3174, 4457)
        };

    public MaidenNylo(int id, Tile tile, MaidenProcess maiden) {
        super(id, tile);
        this.maiden = maiden;
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    @Override
    public void postSequence() {
        if (timer > 0) {
            timer--;
            if (timer == 0) {
                exploded = true;
                maiden.healHit(this, this.hp());
                die();
            }
        }
        if (maiden != null) {
            this.face(maiden);
            this.getMovement().walkTo(maiden.getX(), maiden.getY());
        }
    }

    @Override
    public void die() {
        this.timer = 30;
        this.exploded = false;
        World.getWorld().unregisterNpc(this);
    }
}
