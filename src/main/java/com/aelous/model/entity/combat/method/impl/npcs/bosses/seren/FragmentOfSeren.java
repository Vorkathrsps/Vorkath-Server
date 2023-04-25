package com.aelous.model.entity.combat.method.impl.npcs.bosses.seren;

import com.aelous.cache.definitions.NpcDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.SplatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.Direction;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.NPCCombatInfo;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.utility.chainedwork.Chain;

import java.util.ArrayList;
import java.util.List;

public class FragmentOfSeren extends CommonCombatMethod {

    private int attacks = 0;
    private boolean tornadoAttack = false;
    private NpcDefinition def;
    private int hp;
    private NPCCombatInfo combatInfo;

    public int hp() {
        return hp;
    }

    public void hp(int hp, int exceed) {
        this.hp = Math.min(maxHp() + exceed, hp);
    }

    public int maxHp() {
        return combatInfo == null ? 50 : combatInfo.stats.hitpoints;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && World.getWorld().rollDie(2, 1)) {
            if (World.getWorld().rollDie(2, 1)) {
                meleeClawAttack(entity, target);
            } else {
                rangeAttack();
            }
        } else {
            var roll = World.getWorld().random(8);

            switch (roll) {
                case 0, 1 -> hideAttack();
                case 2, 3 -> magicAttack(entity, target);
                case 4, 5 -> rangeAttack();
                case 6, 7, 8 -> tornadoAttack(entity, target);
            }
        }
        return true;
    } //8380

    private void meleeClawAttack(Entity entity, Entity target) {
        if (entity.dead()) {
            return;
        }
        entity.forceChat("GET BACK!");
        entity.animate(8380);
        entity.setPositionToFace(null); // Stop facing the target
        Chain.bound(null).runFn(8, () -> {
            if (entity.isRegistered() && !entity.dead() && target != null && target.tile().inSqRadius(entity.tile(), 13)) {
                int first = World.getWorld().random(1, 30);
                int second = first / 2;
                target.hit(entity, first, 1);
                target.hit(entity, second, 1);
            }
        });
        entity.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    private void magicAttack(Entity entity, Entity target) {
        if (entity.dead()) {
            return;
        }
        entity.forceChat("Your life is mine mortal!");
        entity.animate(8379);
        var tileDist = entity.tile().transform(1, 1, 0).getChevDistance(target.tile());
        var delay = Math.max(1, (50 + (tileDist * 12)) / 30);
        World.getWorld().getPlayers().forEach(p -> Chain.bound(null).runFn(3, () -> {
            if (p != null && target.tile().inSqRadius(p.tile(), 12)) {
                new Projectile(entity, p, 1702, entity.getProjectileHitDelay(target), entity.projectileSpeed(target), 50, 43, 0, 14, 5).sendProjectile();
                p.hit(entity, CombatFactory.calcDamageFromType(entity, p, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy().submit();
                target.performGraphic(new Graphic(1704, GraphicHeight.MIDDLE, delay + 1));
            }
        }));
    }

    private void rangeAttack() {
        if (entity.dead()) {
            return;
        }
        entity.animate(8376);
        entity.forceChat("You'll never defeat me..");
        entity.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> Chain.bound(null).runFn(2, () -> {
            if (entity.isRegistered() && !entity.dead() && p != null && p.tile().inSqRadius(entity.tile(), 12)) {
                int tileDist = entity.tile().transform(1, 1, 0).getChevDistance(p.tile());
                var delay = Math.max(1, (50 + (tileDist * 12)) / 30);

                new Projectile(entity, p, 1712, entity.getProjectileHitDelay(target), entity.projectileSpeed(target), 50, 43, 0, 14, 0).sendProjectile();

                p.hit(entity, CombatFactory.calcDamageFromType(entity, p, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
            }
        }));
        World.getWorld().getPlayers().forEach(p -> Chain.bound(null).runFn(3, () -> {
            if (entity.isRegistered() && !entity.dead() && p != null && p.tile().inSqRadius(entity.tile(), 12)) {
                int tileDist = entity.tile().transform(1, 1, 0).getChevDistance(p.tile());
                var delay = Math.max(1, (50 + (tileDist * 12)) / 30);

                new Projectile(entity, p, 1712, entity.getProjectileHitDelay(target), entity.projectileSpeed(target), 50, 43, 0, 14, 0).sendProjectile();

                p.hit(entity, CombatFactory.calcDamageFromType(entity, p, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();
            }
        }));
        entity.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    private void hideAttack() {
        if(entity.dead()) {
            return;
        }
        Tile targetTile = target.tile().copy();

        Chain.bound(null).name("SerenHideTask").runFn(1, () -> {
            entity.setPositionToFace(target.tile()); // Face the target.
            entity.animate(8373);
            entity.lockNoDamage();
            target.message(Color.RED.wrap("The Seren Has Targeted You."));
        }).then(1, () -> {
            ((NPC) entity).hidden(true);// removes from client vie// just sets new location, doesn't do any npc updating changes (npc doesn't support TELEPORT like players do)
        }).then(3, () -> {
            entity.forceChat("Taste my wrath!");
            entity.animate(8374);
            ((NPC) entity).hidden(false);
            entity.setPositionToFace(target.tile());
            entity.unlock();
            entity.getCombat().attack(target);
            if (target.tile().inSqRadius(targetTile, 12))
                target.hit(entity, World.getWorld().random(55), 1);
        });
        entity.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    private void tornadoAttack(Entity entity, Entity target) {
        if (entity.dead()) {
            return;
        }
        World.getWorld().getPlayers().forEach(p -> {
            if (p != null && target.tile().inSqRadius(p.tile(), 12)) {
                entity.animate(8378);
                Tile base = entity.tile().copy();
                var tileDist = entity.tile().transform(1, 1, 0).getChevDistance(target.tile());
                var delay = Math.max(1, (50 + (tileDist * 12)) / 30);

                entity.forceChat("Run my child...");

                final List<Tile> crystalSpots = new ArrayList<>(List.of(new Tile(0, 6, 0)));

                if (entity.hp() < 750) {
                    crystalSpots.add(new Tile(3, 6, 0));
                }

                if (entity.hp() < 500) {
                    crystalSpots.add(new Tile(World.getWorld().random(1, 4), World.getWorld().random(1, 4), 0));
                }

                if (entity.hp() < 250) {
                    crystalSpots.add(new Tile(World.getWorld().random(3, 7), World.getWorld().random(2, 6), 0));
                }

                Tile centralCrystalSpot = new Tile(entity.getX(), entity.getY(), 0);
                Tile central = base.transform(centralCrystalSpot.x, centralCrystalSpot.y);
                ArrayList<Tile> spots = new ArrayList<>(crystalSpots);
                int[] ticker = new int[1];
                Chain.bound(null).runFn(2, () -> World.getWorld().tileGraphic(1718, central, 0, delay)).repeatingTask(1, t -> {
                    if (ticker[0] == 10) {
                        t.stop();
                        return;
                    }
                    for (Tile spot : spots) {
                        World.getWorld().tileGraphic(1718, base.transform(spot.x, spot.y), 0, delay);
                    }
                    ArrayList<Tile> newSpots = new ArrayList<>();
                    for (Tile spot : new ArrayList<>(spots)) {
                        final Tile curSpot = base.transform(spot.x, spot.y);
                        if (curSpot.equals(target.tile())) {
                            target.hit(entity, World.getWorld().random(1, 5), SplatType.HITSPLAT);
                        } else {
                            final Direction direction = Direction.getDirection(curSpot, target.tile());
                            Tile newSpot = spot.transform(direction.x, direction.y);
                            newSpots.add(newSpot);
                        }
                    }
                    spots.clear();
                    spots.addAll(newSpots);
                    ticker[0]++;
                });
            }
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return tornadoAttack ? 8 : entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 5;
    }
}
