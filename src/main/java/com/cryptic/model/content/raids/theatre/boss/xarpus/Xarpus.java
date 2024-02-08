package com.cryptic.model.content.raids.theatre.boss.xarpus;

import com.cryptic.model.World;
import com.cryptic.model.content.mechanics.Poison;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.xarpus.objects.PoisonSplat;
import com.cryptic.model.content.raids.theatre.stage.RoomState;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Area;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @Author: Origin
 * @Date: 7/18/2023
 */
public class Xarpus extends NPC {
    TheatreInstance theatreInstance;
    @Getter
    @Setter
    private boolean entranceAnimationStarted = false;
    @Getter
    @Setter
    private boolean initiated = false;
    private int intervalCount = 0;
    private int splatInterval = 4;
    private int interpolatedDirections = 0;
    List<Tile> poisonTile = new ArrayList<>();
    List<GameObject> objects = new ArrayList<>();
    List<Player> players = new ArrayList<>();
    Tile currentDirection;
    public static final Area XARPUS_AREA = new Area(3177, 4394, 3163, 4380);
    Tile[] tiles_to_face = new Tile[]{
        new Tile(3170, 4394),
        new Tile(3177, 4394),
        new Tile(3177, 4387),
        new Tile(3178, 4379),
        new Tile(3170, 4380),
        new Tile(3163, 4380),
        new Tile(3163, 4387),
        new Tile(3163, 4394)
    };
    Direction[] directions = new Direction[]{
        Direction.SOUTH,
        Direction.SOUTH_WEST,
        Direction.WEST,
        Direction.NORTH_WEST,
        Direction.NORTH,
        Direction.NORTH_EAST,
        Direction.EAST,
        Direction.SOUTH_EAST
    };

    public Xarpus(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.setSize(5);
        this.setCombatMethod(null);
        this.spawnDirection(Direction.SOUTH.toInteger());
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.setIgnoreOccupiedTiles(true);
        this.getMovementQueue().setBlockMovement(true);
    }

    public void interpolateQuadrants() { //TODO discover some way to find LOS via deltas
        if (interpolatedDirections >= directions.length) {
            interpolatedDirections = 0;
        }
        var nextDirection = directions[interpolatedDirections++];
        var direction = this.getCentrePosition().tileToDir(nextDirection);
        this.setPositionToFace(direction);
        for (var p : theatreInstance.getPlayers()) {
            if (direction.inFrontOf(p.tile()) && CombatFactory.isAttacking(this)) {
                p.hit(this, Utils.random(60, 75), HitMark.POISON);
                p.forceChat("recoil damage");
            }
        }
    }

    public void sendPoisonPool() {
        for (var t : theatreInstance.getPlayers()) {
            if (t == null) continue;
            var tile = t.tile().copy();
            this.faceTarget(t);
            Chain.noCtx().runFn(1, () -> this.face(null));
            this.animate(8059);
            var tileDist = this.tile().distance(tile);
            int duration = (68 + 25 + (10 * tileDist));
            var entityTile = this.tile().transform(1, 1);
            Projectile p = new Projectile(entityTile, tile, 1555, 68, duration, 95, 0, 20, 5, 10);
            p.send(entityTile, tile);
            World.getWorld().tileGraphic(1556, tile, 0, p.getSpeed());
            PoisonSplat poisonSplat = new PoisonSplat(32744, tile, 22, 0);
            Chain.noCtx().runFn(p.getSpeed() / 30 + 1, () -> {
                if (!poisonTile.contains(poisonSplat.tile())) {
                    poisonSplat.spawn();
                    poisonTile.add(poisonSplat.tile());
                    if (!objects.contains(poisonSplat)) {
                        objects.add(poisonSplat);
                    }
                }
            }).then(1, () -> {
                poisonSplat.animate(8068);
            });
        }
    }

    public void faceTarget(Player player) {
        this.face(player);
    }

    public void clear() {
        for (var o : objects) {
            if (o == null) continue;
            o.remove();
            this.poisonTile.clear();
        }
        this.players.clear();
        this.splatInterval = 0;
        this.intervalCount = 0;
        this.setInitiated(false);
        this.setEntranceAnimationStarted(false);
    }

    @Nonnull
    public Player getRandomTarget() {
        Collections.shuffle(this.theatreInstance.getPlayers());
        return Objects.requireNonNull(Utils.randomElement(this.theatreInstance.getPlayers()));
    }

    @Override
    public void postCombatProcess() {
        var owner = theatreInstance.getOwner();
        if (owner == null) owner = getRandomTarget();
        if (!players.contains(owner) && owner.tile().withinArea(XARPUS_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()))) {
            players.add(owner);
        } else if (players.contains(owner) && !owner.tile().withinArea(XARPUS_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()))) {
            players.remove(owner);
        }

        if (this.dead() || !owner.tile().withinArea(XARPUS_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()))) {
            return;
        }

        if (owner.tile().withinArea(XARPUS_AREA.transformArea(0, 0, 0, 0, theatreInstance.getzLevel()))) {
            if (!entranceAnimationStarted) {
                entranceAnimation.run();
                this.setEntranceAnimationStarted(true);
            }

            for (var p : theatreInstance.getPlayers()) {
                if (p == null) continue;
                for (var t : poisonTile) {
                    if (t == null) continue;
                    var currentX = p.tile().getX();
                    var currentY = p.tile().getY();
                    var previousX = p.getPreviousTile().getX();
                    var previousY = p.getPreviousTile().getY();

                    var middleX = (currentX + previousX) / 2;
                    var middleY = (currentY + previousY) / 2;
                    if (t.equals(middleX, middleY) && !p.tile().equals(t) || t.equals(p.tile())) {
                        p.hit(this, Utils.random(4, 8), HitMark.POISON);
                        if (!Poison.poisoned(p)) {
                            p.poison(Utils.random(4, 6), true);
                        }
                    }
                }
            }

            if (this.isInitiated()) {
                intervalCount++;
                splatInterval--;
                if (intervalCount >= 4 && splatInterval <= 0 && !this.dead()) {
                    sendPoisonPool();
                    intervalCount = 0;
                    splatInterval = 4;
                }
            }
        }
    }

    @Override
    public void die() {
        players.clear();
        for (var p : theatreInstance.getPlayers()) {
            if (p == null) continue;
            p.setRoomState(RoomState.COMPLETE);
            p.getTheatreInstance().onRoomStateChanged(p.getRoomState());
        }
        Chain.noCtx().runFn(1, () -> this.animate(8063)).then(3, () -> {
            World.getWorld().unregisterNpc(this);
        }).then(2, this::clear);
    }

    Runnable entranceAnimation = () -> {
        this.lockNoDamage();
        this.animate(8061);
        Chain.noCtx().runFn(1, () -> {
            this.animate(8058);
        }).then(2, () -> {
            this.transmog(8340, true);
            this.setInstancedArea(theatreInstance);
            this.setCombatInfo(World.getWorld().combatInfo(8340));
            this.setHitpoints(this.maxHp());
            this.noRetaliation(true);
        }).then(1, () -> {
            this.unlock();
            this.setInitiated(true);
        });
    };
}
