package com.aelous.model.content.raids.theatre.bloat.handler;

import com.aelous.model.World;
import com.aelous.model.content.raids.theatre.bloat.utils.BloatUtils;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.animations.Animation;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;

import com.aelous.model.map.region.RegionManager;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * @Author: Origin
 * @Date: 7/18/2023
 */
public class BloatProcess extends NPC {
    Player player;
    BloatUtils bloatUtils = new BloatUtils();
    int interpolateTiles = 0;
    int WALK_SLEEP = 8082;
    int DEATH_ANIM = 8085;
    public static Area BLOAT_AREA = new Area(3288, 4455, 3303, 4440);
    Area IGNORED_AREA = new Area(3298, 4445, 3293, 4450);
    public static final int[] LIMB_GRAPHICS = new int[]{1570, 1571, 1572, 1573};
    @Getter @Setter public boolean sleeping;
    @Getter @Setter public boolean running = false;
    @Setter private int dropCounter = 0;
    @Getter int dropInterval = 5;
    @Getter @Setter int cyclesSinceRandomStop = 0;
    @Getter boolean walkingCycleComplete = false;
    BooleanSupplier walkingCycleFinished = () -> walkingCycleComplete;
    List<Tile> graphicTiles = new ArrayList<>();

    private static final Tile[] WALK_TILES = {
        new Tile(3288, 4440, 0), //tile4
        new Tile(3288, 4451, 0), //tile1
        new Tile(3299, 4451, 0), //tile2
        new Tile(3299, 4440, 0), //tile3
    };

    public BloatProcess(int id, Tile tile, Player player) {
        super(id, tile);
        this.player = player;
        this.spawnDirection(0);
        this.setSize(5);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    public void awaken() {
        this.setSleeping(false);
    }

    public void sleep() {
        this.waitUntil(1, walkingCycleFinished, () -> Chain.noCtx().runFn(2, () -> {
            this.setSleeping(true);
            this.lockDamageOk();
            this.animate(new Animation(WALK_SLEEP));
        }).then(34, () -> {
            this.awaken();
            this.unlock();
        }));

    }

    public void swarm() {
        if (ProjectileRoute.allow(this, player.tile())) {
            int tileDist = this.tile().distance(player.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(this, player, 1569, 51, duration, 0, 0, 12, 5, 10);
            final int delay = this.executeProjectile(p);
            Hit hit = Hit.builder(this, player, Utils.random(10, 20), delay, null).setAccurate(true);
            hit.submit();
        }
    }

    public void fallingLimbs() {
        dropCounter++;

        int randomStopInterval = Utils.random(7, 14);

        if (this.getCyclesSinceRandomStop() >= randomStopInterval) {
            sleep();
            this.setCyclesSinceRandomStop(0);
        }

        if (dropCounter >= this.getDropInterval()) {
            if (!this.isSleeping()) {
                cyclesSinceRandomStop++;
            }
            int numGraphics = Utils.random(12, 18);
            for (int i = 0; i < numGraphics; i++) {
                Tile randomTile = bloatUtils.getRandomTile();
                if (bloatUtils.isTileValid(tile, randomTile) && !RegionManager.blocked(randomTile)) {
                    if (!IGNORED_AREA.contains(randomTile)) {
                        World.getWorld().tileGraphic(bloatUtils.getRandomLimbGraphic(), randomTile, 0, 0);
                        graphicTiles.add(randomTile);
                    }
                }
            }
            this.setDropCounter(0);
        } else {
            graphicTiles.clear();
        }
    }

    public void interpolateBloatWalk() {
        for (int index = 0; index < (running ? 2 : 1); index++) {
            this.walkingCycleComplete = false;
            Tile currentTile = this.tile();
            Tile walkToTile = WALK_TILES[interpolateTiles];
            int deltaX = walkToTile.getX() - currentTile.getX();
            int deltaY = walkToTile.getY() - currentTile.getY();
            int nextStepDeltaX = Integer.compare(deltaX, 0);
            int nextStepDeltaY = Integer.compare(deltaY, 0);

            if (nextStepDeltaX == 0 && nextStepDeltaY == 0) {
                interpolateTiles++;
                if (interpolateTiles >= WALK_TILES.length) {
                    interpolateTiles = 0;
                }
                return;
            }

            int nextX = currentTile.getX() + nextStepDeltaX;
            int nextY = currentTile.getY() + nextStepDeltaY;
            this.queueTeleportJump(new Tile(nextX, nextY, currentTile.getZ()));
            this.updateFaceTile(walkToTile);
            this.walkingCycleComplete = true;
        }
    }

    private void updateFaceTile(Tile walkToTile) {
        if (this.getFaceTile() != null) {
            Tile faceTile = new Tile(walkToTile.getX(), walkToTile.getY());
            this.setFaceTile(faceTile);

            if (this.getSize() > 1) {
                faceTile = this.tile().transform(this.getSize() / 2, this.getSize() / 2, 0);
            }

            this.setPositionToFace(faceTile);
        }
    }


    @Override
    public void postSequence() {

        if (!this.isSleeping()) {
            interpolateBloatWalk();
            fallingLimbs();
            swarm();
        }

        for (var t : graphicTiles) {
            if (player.tile().equals(t)) {
                Hit hit = Hit.builder(this, player, Utils.random(30, 50), 5, null).setAccurate(true).postDamage(d -> player.stun(3));
                hit.submit();
            }
        }

        var healthAmount = hp() * 1.0 / (maxHp() * 1.0);

        setRunning(healthAmount <= 0.6D && healthAmount >= 0.4D);
    }

    @Override
    public void die() {
        Chain.noCtx().runFn(1, () -> {
            this.animate(DEATH_ANIM);
        }).then(3, () -> {
            World.getWorld().unregisterNpc(this);
        });
    }

}
