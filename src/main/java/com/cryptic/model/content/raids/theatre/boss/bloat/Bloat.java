package com.cryptic.model.content.raids.theatre.boss.bloat;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.bloat.utils.BloatUtils;
import com.cryptic.model.content.raids.theatre.stage.RoomState;
import com.cryptic.model.content.raids.theatre.stage.TheatreStage;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;

import com.cryptic.model.map.region.RegionManager;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class Bloat extends NPC {
    BloatUtils bloatUtils = new BloatUtils();
    int interpolateTiles = 0;
    int WALK_SLEEP = 8082;
    int DEATH_ANIM = 8085;
    public static final Area BLOAT_AREA = new Area(3303, 4455, 3288, 4440);
    Area IGNORED_AREA = new Area(3298, 4445, 3293, 4450);
    public static final int[] LIMB_GRAPHICS = new int[]{1570, 1571, 1572, 1573};
    @Getter
    @Setter
    public boolean sleeping;
    @Getter
    @Setter
    public boolean running = false;
    @Setter
    private int dropCounter = 0;
    @Getter
    int dropInterval = 5;
    @Getter
    @Setter
    int cyclesSinceRandomStop = 0;
    @Getter
    boolean walkingCycleComplete = false;
    BooleanSupplier walkingCycleFinished = () -> walkingCycleComplete;
    BooleanSupplier isDead = this::dead;
    List<Tile> graphicTiles = new ArrayList<>();
    List<Player> players = new ArrayList<>();
    TheatreInstance theatreInstance;

    private static final Tile[] WALK_TILES = {
        new Tile(3288, 4440, 0),
        new Tile(3288, 4451, 0),
        new Tile(3299, 4451, 0),
        new Tile(3299, 4440, 0),
    };

    public Bloat(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.spawnDirection(0);
        this.setSize(5);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
    }

    protected void awaken() {
        this.setSleeping(false);
    }

    protected void sleep() {
        this.waitUntil(1, walkingCycleFinished, () -> Chain.noCtx().runFn(2, () -> {
            this.clearGraphicTilesOnSleep();
            this.setSleeping(true);
            this.animate(new Animation(WALK_SLEEP));
        }).then(34, this::awaken).cancelWhen(isDead));

    }

    protected void swarm() {
        for (var player : this.theatreInstance.getPlayers()) {
            if (player == null) continue;
            if (!ProjectileRoute.hasLineOfSight(this, player.tile())) continue;
            if (this.isSleeping()) return;
            int tileDist = this.tile().distance(player.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(this, player, 1569, 51, duration, 0, 0, 12, 5, 10);
            final int delay = this.executeProjectile(p);
            Hit hit = Hit.builder(this, player, Utils.random(10, 20), delay, null).setAccurate(true);
            hit.submit();
        }
    }

    protected void fallingLimbs() {
        dropCounter++;

        int randomStopInterval = Utils.random(7, 14);

        if (this.getCyclesSinceRandomStop() >= randomStopInterval) {
            sleep();
            this.setCyclesSinceRandomStop(0);
        }

        if (dropCounter >= this.getDropInterval()) {
            if (!this.isSleeping()) cyclesSinceRandomStop++;
            int numGraphics = Utils.random(12, 18);
            for (int i = 0; i < numGraphics; i++) {
                Tile randomTile = bloatUtils.getRandomTile().transform(0, 0, theatreInstance.getzLevel());
                if (bloatUtils.isTileValid(tile, randomTile) && !RegionManager.blocked(randomTile)) {
                    if (!IGNORED_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()).contains(randomTile)) {
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

    protected void interpolateBloatWalk() {
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

    private void clearGraphicTilesOnSleep() {
        graphicTiles.clear();
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
    public void postCombatProcess() {
        for (Player player : this.theatreInstance.getPlayers()) {
            if (player == null) continue;
            if (!players.contains(player) && player.tile().withinArea(BLOAT_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()))) {
                players.add(player);
            } else if (players.contains(player) && !player.tile().withinArea(BLOAT_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()))) {
                players.remove(player);
            }

            if (!player.tile().withinArea(BLOAT_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()))) {
                interpolateBloatWalk();
            }

            if (!this.isSleeping() && player.tile().withinArea(BLOAT_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()))) {
                interpolateBloatWalk();
                fallingLimbs();
                swarm();
            }

            for (var t : graphicTiles) {
                if (player.tile().equals(t)) {
                    Hit hit = Hit.builder(this, player, Utils.random(30, 50), 5, null).setAccurate(true);
                    hit.submit();
                    player.stun(3);
                }
            }
        }

        var healthAmount = hp() * 1.0 / (maxHp() * 1.0);

        setRunning(healthAmount <= 0.6D && healthAmount >= 0.4D);
    }

    @Override
    public void die() {
        for (Player player : this.theatreInstance.getPlayers()) {
            player.setRoomState(RoomState.COMPLETE);
            player.getTheatreInstance().onRoomStateChanged(player.getRoomState());
        }
        Chain.noCtx().runFn(1, () -> {
            this.animate(DEATH_ANIM);
        }).then(3, () -> {
            if (this.isSleeping() || locked() || !this.graphicTiles.isEmpty()) {
                graphicTiles.clear();
                this.setSleeping(false);
                this.unlock();
            }
            players.clear();
            theatreInstance.getTheatrePhase().setStage(TheatreStage.THREE);
            World.getWorld().unregisterNpc(this);
        });
    }
}
