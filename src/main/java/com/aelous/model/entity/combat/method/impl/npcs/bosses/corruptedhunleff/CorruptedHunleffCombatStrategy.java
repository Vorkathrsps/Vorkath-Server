package com.aelous.model.entity.combat.method.impl.npcs.bosses.corruptedhunleff;

import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;

public class CorruptedHunleffCombatStrategy extends CommonCombatMethod {

    private Task stompTask;

    private void checkStompTask(Entity entity, Entity target) {
        if (stompTask == null) {
            stompTask = new Task("checkHunleffStompTask", 7) {
                @Override
                protected void execute() {
                    if (entity.dead() || !entity.isRegistered() || !entity.getAsNpc().canSeeTarget(entity, target)) {
                        stop();
                        return;
                    }
                    World.getWorld().getPlayers().forEachInRegion(6810, p -> {
                        if (p.boundaryBounds().inside(entity.tile(), entity.getSize())) {
                            stompAttack(entity, target);
                        }
                    });
                }
            }.bind(entity);
            TaskManager.submit(stompTask);
        }
    }

    /**
     * If a player is underneath the Hunllef when it tries to attack the player, the Hunllef will perform a stomp attack which deals very high damage,
     * similar to the stomp attack used by the Corporeal Beast. This is the only attack that does not count towards the 4 attacks.
     */
    public void stompAttack(Entity entity, Entity target) {
        entity.animate(8420);
        target.hit(entity, World.getWorld().random(30, 51), 1, CombatType.MELEE).setAccurate(true).submit();
    }

    private int attacks = 0;
    private boolean tornadoAttack = false;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        checkStompTask(entity, target);
        attacks++;

        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            if(World.getWorld().rollDie(2,1)) {
                entity.animate(8420);
                entity.getAsNpc().getCombatInfo().maxhit = 50;
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).setAccurate(true).submit();
            } else {
                //Standard attacks: The Hunllef uses two main standard attacks, a ranged crystal-like attack and a magic based orb attack.
                // The Hunllef will alternate between the two attack styles every 4 attacks.
                if (attacks == 4) {
                    rangeAttack(entity, target);
                    attacks = 0;
                } else {
                    magicAttack(entity, target);
                }
            }
        } else {
            //Standard attacks: The Hunllef uses two main standard attacks, a ranged crystal-like attack and a magic based orb attack.
            // The Hunllef will alternate between the two attack styles every 4 attacks.
            if (attacks == 4) {
                rangeAttack(entity, target);
                attacks = 0;
            } else {
                magicAttack(entity, target);
            }
        }

        //25% chance to spawn tornados
        if (World.getWorld().rollDie(4, 1)) {
            tornadoAttack = true;
            tornadoAttack(entity, target);
        }
        return true;
    }

    private void rangeAttack(Entity entity, Entity target) {
        var tileDist = entity.tile().transform(1, 1, 0).getChevDistance(target.tile());
        var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
        new Projectile(entity, target,1705, 35,20 * tileDist,45, 30, 0, true).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
    }

    private void magicAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        //25% chance to disable prayers
        if(World.getWorld().rollDie(4,1)) {
            prayerDisableAttack(entity, target);
        } else {
            var tileDist = entity.tile().transform(1, 1, 0).getChevDistance(target.tile());
            var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
            new Projectile(entity, target,1713, 35,20 * tileDist,45, 30, 0, true).sendProjectile();
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
            target.performGraphic(new Graphic(1709, GraphicHeight.HIGH, delay));
        }
    }

    /**
     * When the Hunllef is attacking using magic-based attacks, it has a chance to use an attack which turns off prayers. These attacks use a different game
     * sound, hence it is recommended to use sounds during the fight.
     */
    private void prayerDisableAttack(Entity entity, Entity target) {
        var tileDist = entity.tile().transform(1, 1, 0).getChevDistance(target.tile());
        var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
        new Projectile(entity, target,1708, 35,20 * tileDist,45, 30, 0, true).sendProjectile();
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        if(hit.isAccurate()) {
            Prayers.closeAllPrayers(target);
            if(target.isPlayer()) {
                target.message(Color.RED.wrap("Your prayers have been disabled"));
            }
        }
    }

    /**
     * The Hunllef summons multiple tornados that chase the player and deal high damage if they reach the players location, these tornadoes disappear after
     * a brief period. The number of tornados summoned increases as the Hunllef takes damage.
     */
    private void tornadoAttack(Entity entity, Entity target) {
        entity.animate(8418);
        Tile base = entity.tile().copy();

        final List<Tile> crystalSpots = new ArrayList<>(List.of(new Tile(0, 6, 0)));

        if(entity.hp() < 750) {
            crystalSpots.add(new Tile(3, 6, 0));
        }

        if(entity.hp() < 500) {
            crystalSpots.add(new Tile(World.getWorld().random(1,4), World.getWorld().random(1,4), 0));
        }

        if(entity.hp() < 250) {
            crystalSpots.add(new Tile(World.getWorld().random(3,7), World.getWorld().random(2,6), 0));
        }

        Tile centralCrystalSpot = new Tile(39, 14, 0);
        Tile central = base.transform(centralCrystalSpot.x, centralCrystalSpot.y);
        ArrayList<Tile> spots = new ArrayList<>(crystalSpots);
        int[] ticker = new int[1];
        Chain.bound(null).runFn(2, () -> World.getWorld().tileGraphic(1718, central, 0, 0)).repeatingTask(1, t -> {
            if (ticker[0] == 10) {
                t.stop();
                return;
            }
            for (Tile spot : spots) {
                World.getWorld().tileGraphic(1718, base.transform(spot.x, spot.y), 0, 0);
            }
            ArrayList<Tile> newSpots = new ArrayList<>();
            for (Tile spot : new ArrayList<>(spots)) {
                final Tile curSpot = base.transform(spot.x, spot.y);
                if (curSpot.equals(target.tile())) {
                    target.hit(entity, World.getWorld().random(1, 35), SplatType.HITSPLAT);
                } else {
                    final Direction direction = Direction.getDirection(curSpot, target.tile());
                    Tile newSpot = spot.transform(direction.x, direction.y);
                    newSpots.add(newSpot);
                }
            }
            // visual debug
            /*ArrayList<GroundItem> markers = new ArrayList<>(1);
            for (Tile step : newSpots) {
                GroundItem marker = new GroundItem(new Item(ItemIdentifiers.VIAL, 1), new Tile(base.transform(step.x, step.y).x,
                    base.transform(step.x, step.y).y, mob.getZ()), null);
                GroundItemHandler.createGroundItem(marker);
                markers.add(marker);
            }
            Task.runOnceTask(1, c -> {
                markers.forEach(GroundItemHandler::sendRemoveGroundItem);
            });*/
            spots.clear();
            spots.addAll(newSpots);
            ticker[0]++;
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return tornadoAttack ? 8 : entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
