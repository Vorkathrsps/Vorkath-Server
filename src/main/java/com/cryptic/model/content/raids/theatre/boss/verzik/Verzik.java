package com.cryptic.model.content.raids.theatre.boss.verzik;

import com.cryptic.model.World;
import com.cryptic.model.content.raids.theatre.TheatreInstance;
import com.cryptic.model.content.raids.theatre.boss.verzik.phase.VerzikPhase;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.MovementQueue;
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
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.TREASURE_ROOM;
import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.VERZIKS_THRONE_32737;

public class Verzik extends NPC {
    TheatreInstance theatreInstance;
    @Setter
    VerzikPhase phase;
    int intervalCount = 0;
    int intervals = 0;
    int value = (this.phase == VerzikPhase.ONE) ? 12 : 4;
    int attackCount = 0;
    final int direction = Direction.SOUTH.toInteger();
    Tile destination = new Tile(3166, 4311);
    @Setter boolean doPath = false;
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
        this.face(null);
        this.animate(8109);
        var players = theatreInstance.getPlayers();
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
                for (var npc : theatreInstance.getVerzikPillarNpcs()) {
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
        int vecX = (p.getAbsX() - Utils.getClosestX(this, p.tile()));
        if (vecX != 0)
            vecX /= Math.abs(vecX);
        int vecY = (p.getAbsY() - Utils.getClosestY(this, p.tile()));
        if (vecY != 0)
            vecY /= Math.abs(vecY);
        int endX = p.getAbsX();
        int endY = p.getAbsY();
        for (int i = 0; i < 4; i++) {
            if (DumbRoute.getDirection(endX, endY, theatreInstance.getzLevel(), p.getSize(), endX + vecX, endY + vecY) != null) {
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
            p.stun(8);
        }
    }

    public void sequenceSlamAndElectricity() {
        attackCount++;
        var target = Utils.randomElement(theatreInstance.getPlayers());
        var tile = target.tile();
        this.getCombat().setTarget(target);
        this.setPositionToFace(tile);
        this.animate(8114);
        for (var p : theatreInstance.getPlayers()) {
            if (p == null) continue;
            if (RouteMisc.getEffectiveDistance(p, this) <= 1) {
                System.out.println("true");
                //sendKnockBack(p);
                break;
            }
            sendToxicBlast(p);
        }
    }

    public void sendToxicBlast(Player p) {
        var tileDist = this.tile().distance(p.tile());
        int duration = (21 + 39 + (tileDist));
        var verzikTile = this.tile().center(this.getSize());
        var playerTile = p.getCentrePosition();
        Projectile projectile = new Projectile(verzikTile, playerTile, 1583, 21, duration, 70, 0, 12, this.getSize(), 128, 0);
        int delay = projectile.send(verzikTile, playerTile);
        Chain.bound(this).name("VerzikSlamTask").runFn(delay, () -> {
            if (p.tile().equals(projectile.getEnd())) {
                p.hit(this, World.getWorld().random(1, 47));
            }
        });
        World.getWorld().tileGraphic(1584, p.tile(), 0, projectile.getSpeed());
    }

    public void sendElectricity(Player p) {
        attackCount = 0;
        var target = Utils.randomElement(theatreInstance.getPlayers());
        var tile = target.tile();
        this.getCombat().setTarget(target);
        this.setPositionToFace(tile);
        this.animate(8114);
        var tileDist = this.tile().distance(p.tile());
        int duration = (21 + 39 + (tileDist));
        Projectile projectile = new Projectile(this, p, 1583, 21, duration, 70, 0, 12, this.getSize(), 128, 0);
        int delay = projectile.send(this, p);
    }
    @Override
    public void postSequence() {
        if (this.id() == VERZIK_VITUR_8369) {
            return;
        }

        if (this.id() == VERZIK_VITUR_8371 && doPath) {
            this.setDoPath(false);
            this.getMovementQueue().reset();
            this.stepAbs(destination.getX(), destination.getY(), MovementQueue.StepType.FORCED_WALK);
            return;
        }

        if (this.phase == VerzikPhase.TRANSITIONING) {
            return;
        }

        intervalCount++;
        intervals--;

        if (intervalCount >= (this.phase == VerzikPhase.ONE ? 12 : 4) && intervals <= 0 && !this.dead()) {
            intervalCount = 0;
            intervals = value;
            switch (this.phase) {
                case ONE -> sendMagicSphere();
                case TWO -> sequenceSlamAndElectricity();
            }
        }
    }
    public void transitionPhaseOne() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        var pillarObjects = theatreInstance.getVerzikPillarObjects();
        var pillarNpcs = theatreInstance.getVerzikPillarNpcs();
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
                throne = new GameObject(VERZIKS_THRONE_32737, new Tile(3167, 4324, theatreInstance.getzLevel()), 10, 0);
                throne.spawn();
                this.animate(8112);
                this.transmog(8371);
                this.setPositionToFace(destination.center(5).tileToDir(direction));
            }).then(2, () -> {
                this.animate(-1);
                this.setDoPath(true);
            }).then(15, () -> {
                this.transmog(8372);
                this.setHitpoints(3250);
                this.heal(this.maxHp());
                this.getMovementQueue().reset();
                this.setPhase(VerzikPhase.TWO);
            });
    }
    public void transitionPhaseTwo() {
        this.setPhase(VerzikPhase.TRANSITIONING);
        this.animate(8118);
        Chain.noCtx().delay(3, () -> {
            this.animate(8119);
            this.transmog(8373);
        }).then(4, () -> {
            this.setPhase(VerzikPhase.THREE);
            this.animate(-1);
            this.transmog(8374);
            this.heal(this.maxHp());
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
            GameObject throne_two = new GameObject(TREASURE_ROOM, new Tile(throne.getX(), throne.getY(), theatreInstance.getzLevel()), 10, 0);
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
