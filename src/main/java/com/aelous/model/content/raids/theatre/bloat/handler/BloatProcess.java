package com.aelous.model.content.raids.theatre.bloat.handler;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;

import lombok.Getter;
import lombok.Setter;

public class BloatProcess extends NPC {
    Player player;
    int WALK_ANIM = 8081;
    int RUN_ANIM = 9031;
    int WALK_SLEEP = 8082;
    int DEATH_ANIM = 8085;
    Area BLOAT_AREA = new Area(3288, 4455, 3303, 4440);
    Tile startingPoint = new Tile(3299, 4444, 0);
    Tile[] tiles = new Tile[]
        {
            new Tile(3301, 4440, 0),
            new Tile(3301, 4450, 0),
            new Tile(3290, 4450, 0),
            new Tile(3290, 4440, 0)
        };

    public BloatProcess(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        //this.spawnDirection(0);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    public void sleep() {

    }

    public void awaken() {

    }

    @Getter
    @Setter
    boolean walking;
    boolean running;
    int sleepCycle;
    int interpolateTiles = 0;

    private static Tile[] WALK_TILES = {
        new Tile(3288, 4440, 0), //tile4
        new Tile(3288, 4451, 0), //tile1
        new Tile(3299, 4451, 0), //tile2
        new Tile(3299, 4440, 0), //tile3
    };

    private static final Area[] SQUARES = {
        new Area(3288, 4440, 3292, 4455, 0),
        new Area(3288, 4440, 3303, 4444, 0),
        new Area(3299, 4440, 3303, 4455, 0),
        new Area(3288, 4451, 3303, 4455, 0),
    };

    public void startBloatWalk() {
        for (int index = 0; index < 2; index++) {
            if (interpolateTiles == 4) {
                interpolateTiles = 0;
            }
            Tile currentTile = this.tile();
            Tile walkToTile = WALK_TILES[interpolateTiles];

            int deltaX = walkToTile.getX() - currentTile.getX();
            int deltaY = walkToTile.getY() - currentTile.getY();

            int nextStepDeltaX = Integer.compare(deltaX, 0);
            int nextStepDeltaY = Integer.compare(deltaY, 0);

            if (nextStepDeltaX == 0 && nextStepDeltaY == 0) {
                interpolateTiles++;
                break;
            }

            int nextX = currentTile.getX() + nextStepDeltaX;
            int nextY = currentTile.getY() + nextStepDeltaY;

            this.queueLegacyTeleport(new Tile(nextX, nextY, currentTile.getZ()));

        }
    }


    @Override
    public void postSequence() {
        startBloatWalk();

    }

    @Override
    public void die() {
        World.getWorld().unregisterNpc(this);
    }

}
