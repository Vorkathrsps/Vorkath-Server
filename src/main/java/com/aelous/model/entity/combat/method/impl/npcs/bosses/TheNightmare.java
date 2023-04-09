package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Color;
import com.aelous.utility.chainedwork.Chain;

public class TheNightmare extends CommonCombatMethod {

    private enum Attacks {
        MELEE, RANGE, MAGIC, SPEED_ATTACK, HIDE_ATTACK, SPECIAL_ATTACK
    }

    private Attacks attack = Attacks.MELEE;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && World.getWorld().rollDie(2, 1)) {
            if(World.getWorld().rollDie(2,1)) {
                meleeClawAttack();//Neither of these hits are protectable
            } else {
                speedAttack();//One of these hits is protectable
            }
        } else {
            var roll = World.getWorld().random(8);

            switch (roll) {
                case 0, 1 -> hideAttack();
                case 2, 3 -> magicAttack();
                case 4, 5 -> rangeAttack();
                case 6, 7, 8 -> specialAttack();
            }
        }
        return true;
    }

    private void meleeClawAttack() {
        attack = Attacks.MELEE;
        entity.animate(8594);
        entity.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> {
            if (entity.isRegistered() && !entity.dead() && p != null && p.tile().inSqRadius(entity.tile(), 12)) {
                int first = World.getWorld().random(1, 30);
                int second = first / 2;
                int third = second / 2;
                int fourth = third / 2;
                p.hit(entity, first, 1);
                p.hit(entity, second, 1);
                p.hit(entity, third, 2);
                p.hit(entity, fourth, 2);
            }
        });
        entity.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    private void speedAttack() {
        attack = Attacks.SPEED_ATTACK;
        entity.animate(8597);
        entity.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> {
            if (entity.isRegistered() && !entity.dead() && p != null && p.tile().inSqRadius(entity.tile(), 12)) {
                entity.forceChat("DEADLY NARUTOOOOOOOOOOO!");
                int second = World.getWorld().random(1, 25);
                p.hit(entity, CombatFactory.calcDamageFromType(entity, p, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy().submit();
                p.hit(entity, second, 2);
            }
        });
        entity.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    private void hideAttack() {
        attack = Attacks.HIDE_ATTACK;
        Tile targetTile = target.tile().copy();

        Chain.bound(null).name("TheNightmareHideAttackTask").runFn(1, () -> {
            entity.setPositionToFace(target.tile()); // Face the target.
            entity.animate(8607);
            entity.lockNoDamage();
            target.message(Color.RED.wrap("The Nightmare has targeted you."));
        }).then(3, () -> {
            ((NPC) entity).hidden(true);// removes from client view
            entity.teleport(targetTile);// just sets new location, doesn't do any npc updating changes (npc doesn't support TELEPORT like players do)
        }).then(3, () -> {
            entity.animate(8609);
            ((NPC) entity).hidden(false);
            entity.setPositionToFace(target.tile());
            entity.unlock();
            entity.getCombat().attack(target);
            if (target.tile().inSqRadius(targetTile, 2))
                target.hit(entity, World.getWorld().random(55), 1);
        });
        entity.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    private void magicAttack() {
        attack = Attacks.MAGIC;
        entity.animate(8598);
        entity.setPositionToFace(null); // Stop facing the target
        Chain.bound(null).runFn(6, () -> {
            World.getWorld().getPlayers().forEach(p -> {
                if (entity.isRegistered() && !entity.dead() && p != null && p.tile().inSqRadius(entity.tile(), 12)) {
                    projectile_bombing(p);
                }
            });
            entity.setPositionToFace(target.tile()); // Go back to facing the target.
        });
    }

    private void projectile_bombing(Player player) {
        int x = player.tile().x;
        int y = player.tile().y;

        Tile projectile_one = new Tile(x, y);
        int projectile_one_distance = entity.tile().distance(projectile_one);
        int projectile_one_delay = Math.max(1, (20 + projectile_one_distance * 12) / 30);

        Tile projectile_two = new Tile(x + World.getWorld().random(2), y + World.getWorld().random(2));
        int projectile_two_distance = entity.tile().distance(projectile_two);
        int projectile_two_delay = Math.max(1, (20 + projectile_two_distance * 12) / 30);

        Tile projectile_three = new Tile(x + World.getWorld().random(2), y + World.getWorld().random(2));
        int projectile_three_distance = entity.tile().distance(projectile_three);
        int projectile_three_delay = Math.max(1, (20 + projectile_three_distance * 12) / 30);

        new Projectile(entity.tile(), projectile_one, 0, 1665, 24 * projectile_one_distance, projectile_one_delay, 50, 0, 0).sendProjectile();
        new Projectile(entity.tile(), projectile_two, 0, 1665, 24 * projectile_two_distance, projectile_two_delay, 50, 0, 0).sendProjectile();
        new Projectile(entity.tile(), projectile_three, 0, 1665, 24 * projectile_three_distance, projectile_three_delay, 50, 0, 0).sendProjectile();

        World.getWorld().tileGraphic(1717, projectile_one, 1, 24 * projectile_one_distance);
        World.getWorld().tileGraphic(1717, projectile_two, 1, 24 * projectile_two_distance);
        World.getWorld().tileGraphic(1717, projectile_three, 1, 24 * projectile_three_distance);

        Chain.bound(null).name("projectile_one_task").runFn(projectile_one_distance, () -> {
            if (player.tile().inSqRadius(projectile_one, 1))
                player.hit(entity, Math.min(20, World.getWorld().random(35)));
            if (player.tile().inSqRadius(projectile_two, 1))
                player.hit(entity, Math.min(20, World.getWorld().random(35)));
            if (player.tile().inSqRadius(projectile_three, 1))
                player.hit(entity, Math.min(20, World.getWorld().random(35)));
        });

        int explosive_x = projectile_two.x;
        int explosive_z = projectile_two.y;

        Tile ricochet_projectile_one = new Tile(explosive_x + World.getWorld().random(2), explosive_z + World.getWorld().random(2));
        int ricochet_projectile_one_distance = projectile_two.distance(ricochet_projectile_one);
        int ricochet_projectile_one_delay = Math.max(1, (20 + ricochet_projectile_one_distance * 12) / 30);

        Tile ricochet_projectile_two = new Tile(explosive_x + World.getWorld().random(2), explosive_z + World.getWorld().random(3));
        int ricochet_projectile_two_distance = projectile_two.distance(ricochet_projectile_two);
        int ricochet_projectile_two_delay = Math.max(1, (20 + ricochet_projectile_two_distance * 12) / 30);

        Chain.bound(null).name("projectile_two_task").runFn(projectile_two_distance, () -> {
            new Projectile(projectile_two, ricochet_projectile_one, 0, 1378, 50 * ricochet_projectile_one_distance, ricochet_projectile_one_delay, 0, 0, 0).sendProjectile();
            new Projectile(projectile_two, ricochet_projectile_two, 0, 1378, 50 * ricochet_projectile_two_distance, ricochet_projectile_two_delay, 0, 0, 0).sendProjectile();

            World.getWorld().tileGraphic(1718, ricochet_projectile_one, 1, 50 * ricochet_projectile_one_distance);
            World.getWorld().tileGraphic(1718, ricochet_projectile_two, 1, 50 * ricochet_projectile_two_distance);

        }).then(ricochet_projectile_one_delay, () -> {
            if (player.tile().inSqRadius(ricochet_projectile_one, 1))
                player.hit(entity, Math.min(20, World.getWorld().random(35)));
            if (player.tile().inSqRadius(ricochet_projectile_two, 1))
                player.hit(entity, Math.min(20, World.getWorld().random(35)));
        });
    }

    private void rangeAttack() {
        attack = Attacks.RANGE;
        entity.animate(8606);
        entity.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> Chain.bound(null).runFn(2, () -> {
            if (entity.isRegistered() && !entity.dead() && p != null && p.tile().inSqRadius(entity.tile(), 12)) {
                int tileDist = entity.tile().transform(1, 1, 0).distance(p.tile());
                var delay = Math.max(1, (50 + (tileDist * 12)) / 30);

                new Projectile(entity, p, 1380, 12 * tileDist, 120, 100, 43, 0, 14, 5).sendProjectile();

                p.hit(entity, CombatFactory.calcDamageFromType(entity, p, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy().submit();

                Chain.bound(null).runFn(3, () -> {
                    p.runOnceTask(5, r -> {
                        final var tile = p.tile().copy();
                        //new Projectile(entity.getCentrePosition(), tile, 1, 1637, 125, 40, 100, 0, 0, 16, 96).sendProjectile();
                        World.getWorld().tileGraphic(1638, tile, 0, 0);
                        if (p.tile().equals(tile)) {
                            for (int hits = 0; hits < 4; hits++) {
                                Chain.bound(null).name("the_nightmare_special_ranged_attack_task").runFn(hits * 3, () -> p.hit(entity, World.getWorld().random(6, 8)));
                            }
                        }
                    });
                });
            }
        }));
        entity.setPositionToFace(target.tile()); // Go back to facing the target.
    }

    private void specialAttack() {
        attack = Attacks.SPECIAL_ATTACK;
        // Go back to facing the target.
        entity.animate(8601);
        entity.setPositionToFace(null); // Stop facing the target
        World.getWorld().getPlayers().forEach(p -> {
            if (entity.isRegistered() && !entity.dead() && p != null && p.tile().inSqRadius(entity.tile(), 12)) {
                Tile tile_one = p.tile();
                Tile tile_two = tile_one.transform(World.getWorld().random(1, 2), World.getWorld().random(1, 2));
                Tile tile_three = tile_one.transform(World.getWorld().random(1, 2), World.getWorld().random(1, 2));
                Tile tile_four = tile_one.transform(World.getWorld().random(1, 2), World.getWorld().random(1, 2));
                Tile tile_five = tile_one.transform(World.getWorld().random(1, 2), World.getWorld().random(1, 2));

                World.getWorld().tileGraphic(1727, tile_one, 100, 0);
                World.getWorld().tileGraphic(1727, tile_two, 100, 0);
                World.getWorld().tileGraphic(1727, tile_three, 100, 0);
                World.getWorld().tileGraphic(1727, tile_four, 100, 0);
                World.getWorld().tileGraphic(1727, tile_five, 100, 0);

                Chain.bound(null).runFn(10, () -> {
                    if (p.tile().inSqRadius(tile_one, 2) || p.tile().inSqRadius(tile_two, 2) || p.tile().inSqRadius(tile_three, 2) || p.tile().inSqRadius(tile_four, 2) || p.tile().inSqRadius(tile_five, 2)) {
                        p.hit(entity, World.getWorld().random(1, 30), 1);
                    }
                });
            }
        });
        entity.setPositionToFace(target.tile()); // Go back to facing the target.
    }
    @Override
    public int getAttackSpeed(Entity entity) {
        return attack.equals(Attacks.MAGIC) ? 12 : entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 12;
    }
}
