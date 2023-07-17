package com.aelous.model.content.raids.theatre.bloat.handler;

import com.aelous.model.World;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.Direction;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.model.map.route.routes.TargetRoute;
import com.aelous.utility.Utils;
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
            new Tile(3301, 4453, 0),
            new Tile(3290, 4453, 0),
            new Tile(3290, 4442, 0),
            new Tile(3301, 4442, 0)
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
    int cycle = 0;

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

    @Override
    public void postSequence() {
        Tile step = this.getCentrePosition();
        for (int index = 0; index < 1; index++) {
            if (walkTo == null || Utils.collides(this.getX(), this.getY(), 5, walkTo.getX(), walkTo.getY(), 1)) {
                cycle = (cycle + 1) & 0x3;
                walkTo = WALK_TILES[cycle]; // new target?, yes
            }

            this.getMovementQueue().clear();
           // System.out.printf("go to %s %n", walkTo);
            this.queueLegacyTeleport(walkTo);
        }
    }

    @Override
    public void die() {
        World.getWorld().unregisterNpc(this);
    }

}
