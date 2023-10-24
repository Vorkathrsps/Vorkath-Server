package com.cryptic.model.content.raids.theatre.boss.verzik;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.verzik.nylocas.PurpleNylocas;
import com.cryptic.model.content.raids.theatre.boss.verzik.phase.VerzikPhase;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.route.RouteMisc;
import com.cryptic.model.map.route.routes.DumbRoute;
import com.cryptic.model.map.route.routes.ProjectileRoute;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.TREASURE_ROOM;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.VERZIKS_THRONE_32737;

public class Verzik extends NPC {
    @Getter
    TheatreInstance theatreInstance;
    @Setter
    VerzikPhase phase;
    @Getter
    @Setter
    AtomicInteger walkCount = new AtomicInteger(0);
    @Getter
    @Setter
    AtomicInteger intervalCount = new AtomicInteger(0);
    @Getter
    @Setter
    AtomicInteger intervals = new AtomicInteger(0);
    int value = (this.phase == VerzikPhase.ONE) ? 12 : 4;
    @Setter
    int attackCount = 0;
    final int direction = Direction.SOUTH.toInteger();
    Tile destination = new Tile(3166, 4311);
    @Getter
    @Setter
    boolean pathing = false;
    List<Player> players = new ArrayList<>();
    GameObject throne;

    public Verzik(int id, Tile tile, TheatreInstance theatreInstance) {
        super(id, tile);
        this.theatreInstance = theatreInstance;
        this.spawnDirection(direction);
        this.setCombatMethod(null);
        this.noRetaliation(true);
        this.getCombat().setAutoRetaliate(false);
        this.phase = VerzikPhase.ONE;
    }

    public void sendMagicSphere() {
        if (!this.phase.equals(VerzikPhase.ONE)) {
            return;
        }
        this.face(null);
        this.animate(8109);
        var players = this.getTheatreInstance().getPlayers();
        for (var player : players) {
            if (player == null) continue;
            var position = player.tile();
            var tileDist = this.tile().distance(position);
            int duration = (20 + (10 * tileDist));
            Chain.noCtx().delay(2, () -> {
                Projectile projectile;
                Entity target = player;
                Hit hit;
                boolean lineOfSight = ProjectileRoute.hasLineOfSight(this, player.tile());
                for (var npc : this.getTheatreInstance().getVerzikPillarNpcs()) {
                    var playerSwTile = player.getCentrePosition().getSouthwestTile(npc);
                    var pillarSwTile = npc.getCentrePosition().getSouthwestTile(player);
                    if (player.tile().getX() <= 3168) {
                        if (playerSwTile.isWithinDistance(pillarSwTile, 2) && !lineOfSight) {
                            if (pillarSwTile.isWithinDistance(playerSwTile, 1)) {
                                target = npc;
                                break;
                            }
                        }
                    } else {
                        var playerSeTile = player.getCentrePosition().getSouthEastTile(npc);
                        var pillarSeTile = npc.getCentrePosition().getSouthEastTile(player);
                        if (playerSeTile.isWithinDistance(pillarSeTile, 2) && !lineOfSight) {
                            if (pillarSwTile.isWithinDistance(playerSeTile, 1)) {
                                target = npc;
                                break;
                            }
                        }
                    }
                }
                projectile = new Projectile(this, target, 1580, 20, duration, 100, 25, 20, 0, 5, 10);
                int delay = this.executeProjectile(projectile);
                var isUsingPrayer = Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC);
                var damage = Utils.random(10, 137);
                hit = Hit.builder(this, target, isUsingPrayer ? (int) (damage * .50) : damage, delay, CombatType.MAGIC).setAccurate(true);
                hit.submit();
                target.graphic(1582, GraphicHeight.LOW, projectile.getSpeed());
            });
        }
    }

    public void sendKnockBack(Player p) {
        if (!this.phase.equals(VerzikPhase.TWO)) {
            return;
        }
        int vecX = (p.getAbsX() - Utils.getClosestX(this, p.tile()));
        if (vecX != 0)
            vecX /= Math.abs(vecX);
        int vecY = (p.getAbsY() - Utils.getClosestY(this, p.tile()));
        if (vecY != 0)
            vecY /= Math.abs(vecY);
        int endX = p.getAbsX();
        int endY = p.getAbsY();
        for (int i = 0; i < 4; i++) {
            if (DumbRoute.getDirection(endX, endY, this.getTheatreInstance().getzLevel(), p.getSize(), endX + vecX, endY + vecY) != null) {
                endX += vecX;
                endY += vecY;
            } else {
                break;
            }
        }
        Direction dir;
        if (vecX == -1) {
            dir = Direction.EAST;
        } else if (vecX == 1) {
            dir = Direction.WEST;
        } else if (vecY == -1) {
            dir = Direction.NORTH;
        } else {
            dir = Direction.SOUTH;
        }
        if (endX != p.getAbsX() || endY != p.getAbsY()) {
            int diffX = endX - p.getAbsX();
            int diffY = endY - p.getAbsY();
            ForceMovement forceMovement = new ForceMovement(p.tile(), new Tile(diffX, diffY), 30, 60, 1157, dir.toInteger());
            p.setForceMovement(forceMovement);
            p.hit(this, Utils.random(0, 60));
            p.stun(8);
        }
    }

    int sequenceRandomIntervalTick = 0;

    public void sequenceSlamAndElectricity() {
        if (!this.phase.equals(VerzikPhase.TWO)) {
            return;
        }
        attackCount++;
        var target = Utils.randomElement(this.getTheatreInstance().getPlayers());
        var tile = target.tile();
        this.getCombat().setTarget(target);
        this.setPositionToFace(tile);
        this.animate(8114);
        for (var p : this.getTheatreInstance().getPlayers()) {
            if (p == null) continue;
            if (RouteMisc.getEffectiveDistance(p, this) <= 1) {
                sendKnockBack(p);
                break;
            }
            if (attackCount <= 4) {
                sendToxicBlast(p);
            } else {
                this.setAttackCount(0);
                sequenceRandomIntervalTick++;
                if (sequenceRandomIntervalTick >= 6) {
                    sequenceRandomIntervalTick = 0;
                    if (this.getTheatreInstance().getVerzikNylocasList().isEmpty()) {
                        sendAthanatos();
                        return;
                    }
                }
                sendElectricity();
            }
        }
    }

    public void sendToxicBlast(Player p) {
        if (!this.phase.equals(VerzikPhase.TWO)) {
            return;
        }
        var tileDist = this.tile().distanceTo(p.getCentrePosition());
        int duration = (int) (21 + 39 + (tileDist));
        var verzikTile = this.tile().center(this.getSize());
        var playerTile = p.getCentrePosition();
        Projectile projectile = new Projectile(verzikTile, playerTile, 1583, 21, duration, 70, 0, 12, this.getSize(), 128, 0);
        int delay = projectile.send(this, playerTile);
        Chain.bound(this).name("VerzikSlamTask").runFn(delay, () -> {
            if (p.tile().equals(projectile.getEnd())) {
                p.hit(this, World.getWorld().random(1, 47));
            }
        });
        World.getWorld().tileGraphic(1584, p.tile(), 0, projectile.getSpeed());
    }

    public void sendAthanatos() {
        if (!this.phase.equals(VerzikPhase.TWO)) {
            return;
        }
        var randomTile = World.getWorld().randomTileAround(this.tile, 8);
        if (randomTile != null) {
            var tileDist = this.tile().distanceTo(randomTile);
            int duration = (int) (21 + 159 + (tileDist));
            var verzikTile = this.tile().center(this.getSize());
            Projectile projectile = new Projectile(verzikTile, randomTile, 1586, 21, duration, 70, 1, 12, this.getSize(), 64, 0);
            final int projectileDelay = projectile.send(this, randomTile);
            AtomicReference<PurpleNylocas> purpleNylocas = new AtomicReference<>();
            Chain.noCtx().runFn(projectileDelay, () -> {
                var finalTile = new Tile(randomTile.getX(), randomTile.getY()).transform(0, 0, this.getTheatreInstance().getzLevel());
                for (var p : this.getTheatreInstance().getPlayers()) {
                    if (p.tile().equals(finalTile)) {
                        p.hit(this, Utils.random(78));
                    }
                }
                purpleNylocas.set(new PurpleNylocas(8384, finalTile, this.getTheatreInstance()));
                purpleNylocas.get().setInstance(this.getTheatreInstance());
                purpleNylocas.get().noRetaliation(true);
                purpleNylocas.get().spawn(false);
                purpleNylocas.get().face(this);
                purpleNylocas.get().animate(8079);
                purpleNylocas.get().graphic(1590);
                this.getTheatreInstance().getVerzikNylocasList().add(purpleNylocas.get());
            }).then(5, () -> {
                var dist = purpleNylocas.get().tile().distanceTo(this.tile);
                var dur = (int) (3 + 21 + (dist));
                Projectile nyloProjectile = new Projectile(purpleNylocas.get(), this, 1587, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
                final int nyloDelay = purpleNylocas.get().executeProjectile(nyloProjectile);
                this.healHit(purpleNylocas.get(), 50, nyloDelay);
            }).repeatingTask(6, heal -> {
                var dist = purpleNylocas.get().tile().distanceTo(this.tile);
                var dur = (int) (3 + 21 + (dist));
                if (purpleNylocas.get().dead()) {
                    Projectile nyloProjectile = new Projectile(purpleNylocas.get(), this, 1588, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
                    final int nyloDelay = purpleNylocas.get().executeProjectile(nyloProjectile);
                    this.hit(purpleNylocas.get(), 75, nyloDelay);
                    heal.stop();
                    return;
                }
                Projectile nyloProjectile = new Projectile(purpleNylocas.get(), this, 1587, 3, dur, 5, 70, 2, this.getSize(), 0, 0);
                final int nyloDelay = purpleNylocas.get().executeProjectile(nyloProjectile);
                this.healHit(purpleNylocas.get(), 50, nyloDelay);
            });
        }
    }

    public void sendElectricity() {
        if (!this.phase.equals(VerzikPhase.TWO)) {
            return;
        }
        attackCount = 0;
        var target = Utils.randomElement(this.getTheatreInstance().getPlayers());
        if (target != null) {
            var tile = target.tile();
            this.getCombat().setTarget(target);
            this.setPositionToFace(tile);
            this.animate(8114);
            var tileDist = this.tile().distance(target.tile());
            int duration = (21 + 39 + (5 * tileDist));
            Projectile projectile = new Projectile(this, target, 1585, 21, duration, 70, 24, 12, this.getSize(), 128, 5);
            int delay = projectile.send(this, target);
            Hit hit = Hit.builder(this, target, CombatFactory.calcDamageFromType(this, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
            hit.submit();
        }
    }

    public void interpolatePhaseTwoTransition() {
        for (int index = 0; index < 2; index++) {
            Tile currentTile = this.tile();
            var dst = destination;
            int deltaX = dst.getX() - currentTile.getX();
            int deltaY = dst.getY() - currentTile.getY();
            int nextStepDeltaX = Integer.compare(deltaX, 0);
            int nextStepDeltaY = Integer.compare(deltaY, 0);
            int nextX = currentTile.getX() + nextStepDeltaX;
            int nextY = currentTile.getY() + nextStepDeltaY;
            if (nextStepDeltaX == 0 && nextStepDeltaY == 0) {
                Chain.noCtx().runFn(1, () -> {
                    this.queueTeleportJump(destination.transform(1, 1, this.getTheatreInstance().getzLevel()));
                    this.transmog(8372);
                    this.heal(this.maxHp());
                    this.setPathing(false);
                    this.setPhase(VerzikPhase.TWO);
                });
                return;
            }
            this.queueTeleportJump(new Tile(nextX, nextY, this.getTheatreInstance().getzLevel()));
        }
    }

    @Override
    public void postSequence() {
        if (this.id() == VERZIK_VITUR_8369) {
            return;
        }

        if (this.id() == VERZIK_VITUR_8371 && this.isPathing()) {
            this.getWalkCount().getAndIncrement();
            if (this.getWalkCount().get() >= 2) {
                this.getWalkCount().getAndSet(0);
                interpolatePhaseTwoTransition();
            }
            return;
        }

        if (this.phase == VerzikPhase.TRANSITIONING) {
            return;
        }

        this.getIntervalCount().getAndIncrement();
        this.getIntervals().getAndDecrement();
        if (this.getIntervalCount().get() >= (this.phase == VerzikPhase.ONE ? 12 : 4) && this.getIntervals().get() <= 0 && !this.dead()) {
            this.getIntervalCount().getAndSet(0);
            this.getIntervals().getAndSet(value);
            switch (this.phase) {
                case ONE -> sendMagicSphere();
                case TWO -> sequenceSlamAndElectricity();
            }
        }

    }

    public void transitionPhaseOne() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        var pillarObjects = this.getTheatreInstance().getVerzikPillarObjects();
        var pillarNpcs = this.getTheatreInstance().getVerzikPillarNpcs();
        pillarObjects.stream()
            .filter(o -> o.getId() == 32687)
            .forEach(o -> {
                o.setId(32688);
                Chain.noCtx()
                    .delay(1, () ->
                        players.stream()
                            .filter(Objects::nonNull)
                            .filter(player -> player.tile().isWithinDistance(this.tile(), 1) || o.tile().isWithinDistance(player.tile(), 1))
                            .forEach(player -> {
                                player.hit(this, 10);
                                Direction direction = Direction.SOUTH;
                                ForceMovement forceMovement = new ForceMovement(player.tile(), new Tile(direction.x(), direction.y()), 30, 60, 1114, 0);
                                player.setForceMovement(forceMovement);
                            }))
                    .then(2, () -> o.setId(32689))
                    .then(1, () -> o.animate(8104))
                    .then(2, o::remove);
            });
        pillarObjects.clear();
        pillarNpcs.forEach(NPC::remove);
        pillarNpcs.clear();
        Direction direction = Direction.SOUTH;
        this.animate(8111);
        Chain.noCtx().delay(4, () -> {
            throne = new GameObject(VERZIKS_THRONE_32737, new Tile(3167, 4324, this.getTheatreInstance().getzLevel()), 10, 0);
            throne.spawn();
            this.animate(8112);
            this.transmog(8371);
            this.setPositionToFace(destination.center(5).tileToDir(direction));
        }).then(2, () -> {
            this.animate(-1);
            this.setPathing(true);
        });
    }

    public void transitionPhaseTwo() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.canAttack(false);
        this.animate(8118);
        Chain.noCtx().runFn(2, () -> {
            this.animate(8119);
            this.transmog(8373);
        }).then(4, () -> {
            this.canAttack(true);
            this.animate(-1);
            this.transmog(8374);
            this.heal(this.maxHp());
            this.forceChat("Behold my true nature!");
            this.queueTeleportJump(destination.transform(-1, -1, this.getTheatreInstance().getzLevel()));
            this.setPhase(VerzikPhase.THREE);
        });
    }

    public void transitionPhaseThree() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.animate(8128);
        Chain.noCtx().delay(2, () -> {
            this.animate(-1);
            this.transmog(8375);
        }).then(6, () -> {
            throne.animate(8108);
            this.remove();
        }).then(4, () -> {
            GameObject throne_two = new GameObject(TREASURE_ROOM, new Tile(throne.getX(), throne.getY(), this.getTheatreInstance().getzLevel()), 10, 0);
            throne.replaceWith(throne_two, false);
            this.setPhase(VerzikPhase.DEAD);
        });
    }

    @Override
    public void die() {
        switch (phase) {
            case ONE -> transitionPhaseOne();
            case TWO -> transitionPhaseTwo();
            case THREE -> transitionPhaseThree();
        }
    }
}
