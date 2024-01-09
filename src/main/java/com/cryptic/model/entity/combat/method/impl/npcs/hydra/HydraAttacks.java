package com.cryptic.model.entity.combat.method.impl.npcs.hydra;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.hit.HitMark;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.Direction;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import static com.cryptic.model.entity.combat.method.impl.npcs.hydra.HydraChamber.*;

/**
 * The NPC extension for the hydras.
 *
 * @author Gabriel || Wolfsdarker
 */
public enum HydraAttacks {

    RANGED(null) {
        @Override
        public void executeAttack(AlchemicalHydra hydra, Entity target) {
            hydra.animate(hydra.getAttackAnim());
            int tileDist = hydra.tile().transform(3, 3, 0).distance(target.tile());
            int duration = (50 + 11 + (5 * tileDist));
            Projectile p = new Projectile(hydra, target, 1663, 50, duration, 43, 31, 0, hydra.getSize(), 5);
            hydra.executeProjectile(p);
            int delay = (int) (p.getSpeed() / 30D);
            new Hit(hydra, target, CombatFactory.calcDamageFromType(hydra, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true).submit();
        }
    },

    /**
     * The default magic attack.
     */
    MAGIC(null) {
        @Override
        public void executeAttack(AlchemicalHydra hydra, Entity target) {
            hydra.animate(hydra.getAttackAnim());
            int tileDist = hydra.tile().transform(3, 3).getManhattanDistance(target.tile());
            int duration = (50 + 11 + (5 * tileDist));
            Projectile p = new Projectile(hydra, target, 1662, 50, duration, 43, 0, 0, 1, 5);
            hydra.executeProjectile(p);
            int delay = (int) (p.getSpeed() / 30D);
            new Hit(hydra, target, CombatFactory.calcDamageFromType(hydra, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();

        }
    },

    /**
     * The poison pool for the first phase.
     */
    POISON(HydraPhase.GREEN) {
        @Override
        public void executeAttack(AlchemicalHydra hydra, Entity target) {
            var poolAmount = Utils.random(4, 5);
            var pools = getPoolTiles(hydra.baseLocation, target.tile(), poolAmount);
            hydra.animate(8234);

            for (Tile pool : pools) {
                var tileDist = hydra.tile().distance(pool);
                int duration = (50 + -5 + (10 * tileDist));
                Projectile p = new Projectile(hydra, pool, 1644, 50, duration, 105, 0, 0, hydra.getSize(), 10);
                p.send(hydra, pool);
                World.getWorld().tileGraphic(1645, pool, 0, p.getSpeed());
                var graphicId = getPoolGraphic(hydra, pool);
                World.getWorld().tileGraphic(graphicId, pool, 0, p.getSpeed());
            }

            Chain.noCtx().repeatingTask(1, acidTask -> {
                for (Tile pool : pools) {
                    if (pool.equals(target.tile())) {
                        target.hit(hydra, Utils.random(12), HitMark.POISON);
                    }
                }
                if (acidTask.getRunDuration() == 10) {
                    acidTask.stop();
                }
            });
        }
    },

    /**
     * The lightning strike for the second phase.
     */
    LIGHTNING(HydraPhase.BLUE) {
        @Override
        public void executeAttack(AlchemicalHydra hydra, Entity target) {
            hydra.animate(8241);
            Tile base = hydra.baseLocation;

            Tile centralLightningSpot = new Tile(39, 14, 0);
            Tile central = base.transform(centralLightningSpot.x, centralLightningSpot.y);
            ArrayList<Tile> spots = new ArrayList<>(lightningSpots);

            var tileDist = hydra.tile().distance(central);
            int duration = (50 + -5 + (10 * tileDist));
            Projectile p = new Projectile(hydra, central, 1664, 50, duration, 0, 0, 0, hydra.getSize(), 10);
            p.send(hydra, central);
            World.getWorld().tileGraphic(1664, central, 0, p.getSpeed());

            AtomicInteger ticker = new AtomicInteger(0);

            Projectile p2 = null;
            for (var spot : spots) {
                p2 = new Projectile(hydra, base.transform(spot.x, spot.y), 1665, 50, duration, 55, 0, 0, hydra.getSize(), 5);
                p2.send(hydra, base.transform(spot.x, spot.y));
            }

            Projectile finalP = p2;
            Chain.noCtx().repeatingTask(1, t -> {
                if (ticker.get() == 10) {
                    t.stop();
                    return;
                }
                for (Tile spot : spots) {
                    World.getWorld().tileGraphic(1666, base.transform(spot.x, spot.y), 0, finalP.getSpeed());
                }
                ArrayList<Tile> newSpots = new ArrayList<>();
                for (Tile spot : new ArrayList<>(spots)) {
                    final Tile curSpot = base.transform(spot.x, spot.y);

                    if (target.tile().equals(curSpot)) {
                        target.hit(hydra, Utils.random(20), HitMark.POISON);
                        if (target.isPlayer()) {
                            target.message("<col=ff0000>The electricity temporarily paralyzes you!");
                            target.stun(8);
                        }
                    } else {
                        final Direction direction = Direction.getDirection(curSpot, target.tile());

                        Tile newSpot = spot.transform(direction.x, direction.y);

                        newSpots.add(newSpot);
                    }
                }
                spots.clear();
                spots.addAll(newSpots);
                ticker.getAndIncrement();
            });
        }
    },

    /**
     * The wall of fire for the third phase.
     */
    FIRE_WALL(HydraPhase.RED) {
        @Override
        public void executeAttack(AlchemicalHydra hydra, Entity target) {
            Tile base = hydra.baseLocation;
            hydra.walkAndWait(base.transform(hydraSpawnLoc.x, hydraSpawnLoc.y), () -> {
                FireWallSpots fireWallSpots = getWallFireSpots(target.tile(), base);
                ArrayList<Tile> spotOffsets = fireWallSpots.spots;
                ArrayList<Tile> spots = new ArrayList<>();
                int[] ticker = new int[2];
                Chain.bound(null).runFn(1, () -> {
                    Tile faceSpot = base.transform((spotOffsets.get(0).x + spotOffsets.get(1).x) / 2, (spotOffsets.get(0).y + spotOffsets.get(1).y) / 2);
                    hydra.setPositionToFace(faceSpot);
                    hydra.animate(hydra.getAttackAnim());
                    for (int i = 0; i <= 1; i++) {
                        for (Tile spot : fireWallSpots.projectileSpots) {
                            for (int x = fireWallSpots.projectileSpots.get(i * 2).x; x < fireWallSpots.projectileSpots.get(i * 2 + 1).x; x++) {
                                for (int y = fireWallSpots.projectileSpots.get(i * 2).y; y < fireWallSpots.projectileSpots.get(i * 2 + 1).y; y++) {
                                    fireProjectileToLocation(hydra, base.transform(x, y), 1667, 45, 55, 15, 15, 55, 0);
                                }
                            }
                        }
                    }
                }).runFn(1, () -> {
                    for (Tile spot : spots) {
                        if (target.tile() == spot) {
                            target.hit(hydra, Utils.random(20),0);
                        }
                    }
                }).runFn(1, () -> {
                    for (int i = 0; i <= 1; i++) {
                        for (Tile spot : spots) {
                            if (target.tile() == spot) {
                                target.hit(hydra, Utils.random(20),0);
                            }
                        }

                        fireWallSpots.spawnFireWall(base, i, spots);
                    }
                }).repeatingTask(1, t -> {
                    if (t.getRunDuration() == 2) {
                        t.stop();
                        return;
                    }
                    for (Tile spot : spots) {
                        if (target.tile() == spot) {
                            target.hit(hydra, Utils.random(20),0);
                        }
                    }

                    hydra.setPositionToFace(target.tile());
                    Tile startMovingFire = new Tile(Utils.random(spotOffsets.get(0).x, spotOffsets.get(1).x), Utils.random(spotOffsets.get(0).y, Utils.random(spotOffsets.get(1).y)));
                    hydra.animate(hydra.getAttackAnim());
                    fireProjectileToLocation(hydra, base.transform(startMovingFire.x, startMovingFire.y), 1667, 45, 50, 0, 5, 55, 0);
                    for (Tile spot : spots) {
                        if (target.tile() == spot) {
                            target.hit(hydra, Utils.random(20),0);
                        }
                    }

                    spots.add(base.transform(startMovingFire.x, startMovingFire.y, 0));
                    for (int i = 0; i < fireWallDuration; i++) {
                        for (Tile spot : spots) {
                            if (target.tile() == spot) {
                                target.hit(hydra, Utils.random(20),0);
                            }
                        }
                        if (i < 16) {
                            if (base.transform(startMovingFire.x, startMovingFire.y) == target.tile()) {
                                target.hit(hydra, Utils.random(20),0);
                            } else {
                                startMovingFire = getMoveLocation(base, startMovingFire, target.tile());
                                spots.add(startMovingFire);
                                World.getWorld().tileGraphic(1668, base.transform(startMovingFire.x, startMovingFire.y), 0, 0);
                            }
                        } else if (i == 16) {
                            hydra.unlock();
                        }
                    }
                });
            });
        }
    },

    /**
     * The poison pool for the final and enraged version.
     */
    ENRAGED_POISON(HydraPhase.ENRAGED) {
        @Override
        public void executeAttack(AlchemicalHydra hydra, Entity target) {
            var poolAmount = Utils.randomFloat() < 0.6 ? 1 : Utils.random(4, 5);
            var pools = getPoolTiles(hydra.baseLocation, target.tile(), poolAmount);
            hydra.animate(8234);

            for (Tile pool : pools) {
                fireProjectileToLocation(hydra, pool, 1644, 50, 90, 0, 5, 55, 0);
            }

            Chain.bound(null).runFn(3, () -> {
                for (Tile pool : pools) {
                    World.getWorld().tileGraphic(1645, pool, 0, 0);
                }
            }).then(1, () -> {
                for (Tile pool : pools) {
                    var graphicId = getPoolGraphic(hydra, pool);
                    World.getWorld().tileGraphic(graphicId, pool, 0, 0);
                }
            }).repeatingTask(1, t -> {
                for (Tile pool : pools) {
                    if (pool == target.tile()) {
                        target.hit(hydra, Utils.random(12), HitMark.POISON);
                    }
                }
                if (t.getRunDuration() == 10) {
                    t.stop();
                }
            });
        }
    };

    public HydraPhase phaseRequired;

    HydraAttacks(HydraPhase phaseRequired) {
        this.phaseRequired = phaseRequired;
    }

    /**
     * Executes the hydra's attack.
     */
    public abstract void executeAttack(AlchemicalHydra hydra, Entity target);

    /**
     * Fires a projectile from the hydra to the target.
     */
    void fireProjectileToEntity(AlchemicalHydra hydra, Entity target, int projectile, int delay) {
        var center = Utils.getCenterLocation(hydra);
        var dir = Direction.of(target.tile().x - center.x, target.tile().y - center.y);
        var from = center.transform(dir.x * 2, dir.y * 2);
        var speed = Utils.getSpeedModifier(from, target.tile());
       // hydra.executeProjectile(new Projectile(hydra, target, projectile, delay, speed, 55, 25, 0, hydra.getSize()));
    }

    /**
     * Fires a projectile from the hydra to a tile.
     */
    void fireProjectileToLocation(AlchemicalHydra hydra, Tile tile, int projectile, int delay, int projectileSpeed, int angle, int stepness, int startHeight, int endHeight) {
        var center = Utils.getCenterLocation(hydra);
        var dir = Direction.of(tile.x - center.x, tile.y - center.y);
        var from = center.transform(dir.x * 2, dir.y * 2);
       // new Projectile(from, tile, hydra.getProjectileLockonIndex(), projectile, projectileSpeed, delay, startHeight, endHeight, 0, angle, stepness).sendProjectile();
    }

    /**
     * Fires a projectile from a tile to another.
     */
    void fireProjectileToLocation(Tile from, Tile to, int projectile, int delay, int projectileSpeed, int angle, int stepness, int startHeight, int endHeight) {
       // new Projectile(from, to, 0, projectile, projectileSpeed, delay, startHeight, endHeight, 0, angle, stepness).sendProjectile();
    }

    /**
     *hydra_magic_projectile:1662
     * hydra_ranged_projectile:1663
     * hydra_poison_projectile:1644
     * hydra_poison_splash:1645
     * hydra_poison_start:1650
     * hydra_poison_disappear:1651
     * hydra_poison_south_east:1654
     * hydra_poison_south:1655
     * hydra_poison_south_west:1656
     * hydra_poison_west:1657
     * hydra_poison_north_west:1658
     * hydra_poison_north:1659
     * hydra_poison_north_east:1660
     * hydra_poison_east:1661
     */
}
