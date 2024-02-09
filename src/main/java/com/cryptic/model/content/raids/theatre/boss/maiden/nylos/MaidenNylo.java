package com.cryptic.model.content.raids.theatre.boss.maiden.nylos;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.boss.maiden.Maiden;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.map.position.Tile;

public class MaidenNylo extends NPC {
    Maiden maiden;
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

    public MaidenNylo(int id, Tile tile, Maiden maiden) {
        super(id, tile);
        this.maiden = maiden;
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    @Override
    public void combatSequence() {
        if (maiden.dead()) {
            this.die();
            return;
        }

        if (timer > 0) {
            timer--;
            if (timer == 0) {
                maiden.healHit(this, this.hp());
                die();
            }
        }

        if (maiden != null) {
            this.face(maiden);
            this.getMovement().walkTo(maiden.getCentrePosition());
        }

    }

    @Override
    public void die() {
        this.timer = 30;
        World.getWorld().unregisterNpc(this);
    }
}
