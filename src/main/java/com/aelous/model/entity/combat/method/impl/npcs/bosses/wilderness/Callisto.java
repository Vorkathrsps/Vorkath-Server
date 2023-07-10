package com.aelous.model.entity.combat.method.impl.npcs.bosses.wilderness;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.World;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.equipment.EquipmentInfo;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Area;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.route.routes.DumbRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;

/**
 * @Author: Origin
 * @Date: 7/9/2023
 */
public class Callisto extends CommonCombatMethod {

    //TODO if you run 2 tiles past traps you can avoid the damage

    private int roarCount = 0;
    private int trapState = 0;
    public boolean performingAnimation = false;
    private ArrayList<Tile> allActiveTraps = new ArrayList<>();
    private ArrayList<GameObject> allActiveTrapObjects = new ArrayList<>();
    private final Area ARTIO_AREA = new Area(1747, 11534, 1769, 11553);

    @Override
    public int moveCloseToTargetTileRange(@NonNull final Entity entity) {
        return 10;
    }

    @Override
    public int getAttackSpeed(@NonNull final Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public boolean customOnDeath(Hit hit) {
        NPC entity = (NPC) this.entity;
        entity.die();
        roarCount = 0;
        trapState = 0;
        performingAnimation = false;
        for (var o : allActiveTrapObjects) {
            Chain.noCtx().runFn(3, () -> o.animate(9999)).then(2, o::remove);
        }
        return true;
    }

    @Override
    public void process(Entity entity, Entity target) {
        var bear = NpcIdentifiers.CALLISTO;
        var npc = (NPC) entity;
        if (npc.getId() == bear) {
            return;
        }
        if (target != null) {
            var targTile = target.tile().copy();
            allActiveTrapObjects.stream().filter(o -> o.tile().equals(targTile)).findFirst().ifPresent(o -> {
                Chain.noCtx().runFn(1, () -> {
                    o.animate(9999);
                }).then(1, () -> {
                    target.hit(entity, Utils.random(1, 15), 1);
                    o.remove();
                    allActiveTrapObjects.remove(o);
                });
            });
        }
    }

    @Override
    public boolean prepareAttack(@NonNull final Entity entity, @NonNull final Entity target) {
        if (performingAnimation) {
            return false;
        }

        if (Utils.percentageChance(50)) {
            rangeAttack(entity, target);
        } else if (Utils.percentageChance(35)) {
            magicAttack(entity, target);
        } else {
            meleeAttack(entity, target);
        }

        trapState++;

        double hpPercentage = ((double) entity.hp() / entity.maxHp());
        if (hpPercentage <= .66 && trapState == Utils.random(1, 2) || hpPercentage <= .66 && roarCount == 0) {
            bearTraps(entity);
        }
        if (hpPercentage <= .33 && trapState == Utils.random(1, 2) || hpPercentage <= .33 && roarCount == 1) {
            bearTraps(entity);
        }

        if (trapState == 2) {
            trapState = 0;
        }

        return true;
    }

    @Override
    public void doFollowLogic() {
        if (performingAnimation) {
            return;
        }
        if (!withinDistance(1) && !entity.frozen()) {
            var tile = target.tile().transform(1, 1, 0);
            entity.getMovement().step(tile.getX(), tile.getY(), MovementQueue.StepType.REGULAR);
        }
        follow(1);
    }

    private void meleeAttack(@NonNull final Entity entity, @NonNull final Entity target) {
        if (!withinDistance(2) || performingAnimation) {
            return;
        }
        entity.animate(10012);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 3, CombatType.MELEE).checkAccuracy();
        hit.submit();
    }

    private void rangeAttack(@NonNull final Entity entity, @NonNull final Entity target) {
        if (!withinDistance(10) || performingAnimation) {
            return;
        }
        entity.animate(10013);
        entity.graphic(2349);
        int tileDist = entity.tile().distance(target.tile());
        int duration = (25 + 10 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 2350, 25, duration, 20, 20, 0, 5, 10);
        final int delay = entity.executeProjectile(p);
        var dmg = CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED);
        Hit hit = Hit.builder(entity, target, dmg, delay, CombatType.RANGED).checkAccuracy();
        hit.submit();
        target.graphic(2351, GraphicHeight.LOW, p.getSpeed());
    }

    private void magicAttack(@NonNull final Entity entity, @NonNull final Entity target) {
        if (!withinDistance(10) || performingAnimation) {
            return;
        }
        entity.animate(10014);
        int tileDist = entity.tile().distance(target.tile());
        int duration = (55 + 10 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 133, 55, duration, 50, 31, 0, 5, 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        if (!Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
            knockBack(entity, target);
        }
        if (hit.isAccurate()) {
            target.graphic(134, GraphicHeight.MIDDLE, p.getSpeed());
        }
    }

    private void bearTraps(@NonNull final Entity entity) {
        if (roarCount == 0) {
            roarCount++;
            performingAnimation = true;
            Chain.noCtx().runFn(1, () -> {
                entity.animate(10015);
                entity.graphic(2352);
            }).then(4, () -> {
                entity.unlock();
                performingAnimation = false;
            });
        } else if (roarCount == 1) {
            roarCount++;
            performingAnimation = true;
            Chain.noCtx().runFn(1, () -> {
                entity.animate(10015);
            }).then(3, () -> {
                performingAnimation = false;
            });
        }
        int spawned = 5;
        int attempts = 50;

        ArrayList<Tile> newTraps = new ArrayList<>();

        while (spawned-- > 0 && attempts-- > 0) {
            var t = ARTIO_AREA.randomTile();
            if (!t.allowObjectPlacement()) {
                continue;
            }
            if (allActiveTraps.contains(t)) {
                spawned++;
                continue;
            }
            newTraps.add(t);
        }

        allActiveTraps.addAll(newTraps);

        for (Tile newTrap : newTraps) {
            if (MovementQueue.dumbReachable(newTrap.getX(), newTrap.getY(), entity.tile())) {
                World.getWorld().tileGraphic(2343, newTrap, 0, 0);
                Chain.noCtx().delay(1, () -> {
                    GameObject o = newTrap.object(47146).spawn();
                    allActiveTrapObjects.add(o);
                    o.animate(9998);
                    Chain.noCtx().delay(20, () -> {
                        o.remove();
                        allActiveTrapObjects.remove(o);
                        allActiveTraps.remove(newTrap);
                    });
                });
            }
        }
    }

    private void knockBack(@NonNull final Entity entity, @NonNull final Entity target) {
        int vecX = (target.getAbsX() - Utils.getClosestX(this.entity, target.tile()));
        if (vecX != 0)
            vecX /= Math.abs(vecX);
        int vecY = (target.getAbsY() - Utils.getClosestY(this.entity, target.tile()));
        if (vecY != 0)
            vecY /= Math.abs(vecY);
        int endX = target.getAbsX();
        int endY = target.getAbsY();
        for (int i = 0; i < 4; i++) {
            if (DumbRoute.getDirection(endX, endY, entity.getZ(), target.getSize(), endX + vecX, endY + vecY) != null) {
                endX += vecX;
                endY += vecY;
            } else
                break;
        }
        Direction dir;
        if (vecX == -1)
            dir = Direction.EAST;
        else if (vecX == 1)
            dir = Direction.WEST;
        else if (vecY == -1)
            dir = Direction.NORTH;
        else
            dir = Direction.SOUTH;

        if (endX != target.getAbsX() || endY != target.getAbsY()) {
            if (target.isPlayer()) {
                int finalEndX = endX;
                int finalEndY = endY;
                Chain.bound(null).runFn(1, () -> {
                    final Player p = target.getAsPlayer();
                    p.lock();
                    p.animate(1157);
                    p.hit(entity, World.getWorld().random(50));
                    p.stun(2, true);
                    int diffX = finalEndX - p.getAbsX();
                    int diffY = finalEndY - p.getAbsY();
                    ForceMovement forceMovement = new ForceMovement(target.tile(), new Tile(diffX, diffY), 30, 60, 1157, dir.toInteger());
                    target.setForceMovement(forceMovement);
                    p.unlock();
                });
            }
        }
    }
}
