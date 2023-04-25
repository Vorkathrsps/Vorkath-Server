package com.aelous.model.entity.combat.method.impl.npcs.dragons;

import com.aelous.model.World;
import com.aelous.model.entity.MovementQueue;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * april 29, 2020
 */
public class RuneDragon extends CommonCombatMethod {
    boolean sparkAttack = false;

    @Override
    public boolean prepareAttack(Entity dragon, Entity target) {
        int rand = Utils.random(5);
        NPC npc = (NPC) dragon;
        if (rand == 1) {
            doDragonBreath(npc, target);
        } else if (rand == 2) {
            doRangedAttack(npc, target);
        } else if (rand == 3) {
            doMagicBlast(npc, target);
        } else if (rand == 4) {
            sparkAttack = true;
            sparkAttack(npc, target);
        } else {
            if (CombatFactory.canReach(dragon, CombatFactory.MELEE_COMBAT, target)) {
                doMelee(npc, target);
            } else {
                int roll = Utils.random(3);
                if (roll == 1) {
                    doDragonBreath(npc, target);
                } else if (roll == 2) {
                    doRangedAttack(npc, target);
                } else if (roll == 3) {
                    doMagicBlast(npc, target);
                }
            }
        }
        return true;
    }

    private void sparkAttack(NPC npc, Entity target) {
        npc.animate(81);
        for (int i = 0; i < 2; i++) {
            Tile pos = target.tile().relative(Utils.get(-1, 1), Utils.get(-1, 1));
            NPC spark = new NPC(8032, pos);

            Chain.bound(null).runFn(5, () -> {
                for (int j = 0; j < 5; j++) {
                    World.getWorld().registerNpc(spark);
                    spark.step(World.getWorld().random(-1, 1), World.getWorld().random(-1, 1), MovementQueue.StepType.FORCED_WALK);

                    if (target.tile().isWithinDistance(spark.tile(), 1)) {
                        int maxDamage = 8;
                        if (target.getAsPlayer().getEquipment().contains(ItemIdentifiers.INSULATED_BOOTS))
                            maxDamage = 2;
                        target.hit(entity, maxDamage);
                    }
                }
            }).then(5, () -> {
                World.getWorld().unregisterNpc(spark);
                this.sparkAttack = false;
            });
        }
    }


    private void doMelee(NPC npc, Entity target) {
        npc.animate(npc.attackAnimation());
        target.hit(npc, Utils.random(npc.getCombatInfo().maxhit), CombatType.MELEE).checkAccuracy().submit();
    }

    private void doRangedAttack(NPC npc, Entity target) {
        npc.animate(81);
        int damage = Utils.random(npc.getCombatInfo().maxhit);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 1486, 41, duration, 43, 31, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);

        if (Utils.rollDie(5, 2)) {
            target.hit(npc, damage, delay, CombatType.RANGED).submit();
            npc.heal(damage, npc.maxHp());
        } else {
            //Regular ranged attack
            target.hit(npc, damage, delay, CombatType.RANGED).checkAccuracy().submit();
        }
    }

    private void doMagicBlast(NPC npc, Entity target) {
        npc.animate(81);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 162, 51, duration, 43, 31, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        target.hit(npc, Utils.random(npc.getCombatInfo().maxhit), delay, CombatType.MAGIC).checkAccuracy().submit();
    }

    private void doDragonBreath(NPC npc, Entity target) {
        if(target instanceof Player player) {
            double max = 50.0;
            int antifire_charges = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire_charges > 0;

            boolean memberEffect = player.getMemberRights().isExtremeMemberOrGreater(player) && !WildernessArea.inWild(player);
            if (player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false) || memberEffect) {
                player.message("Your super antifire potion protects you completely from the heat of the dragon's breath!");
                max = 0.0;
            }

            //Does our player have an anti-dragon shield?
            if (max > 0 && hasShield) {
                player.message("Your shield absorbs most of the dragon fire!");
                max *= 0.3;
            }

            //Has our player recently consumed an antifire potion?
            if (max > 0 && hasPotion) {
                player.message("Your potion protects you from the heat of the dragon's breath!");
                max *= 0.3;
            }

            //Is our player using protect from magic?
            if (max > 0 && Prayers.usingPrayer(player, Prayers.PROTECT_FROM_MAGIC)) {
                player.message("Your prayer absorbs most of the dragon's breath!");
                max *= 0.6;
            }

            if (hasShield && hasPotion) {
                max = 0.0;
            }


            entity.animate(81);
            int hit = Utils.random((int) max);
            var tileDist = entity.tile().distance(target.tile());
            int duration = (41 + 11 + (5 * tileDist));
            Projectile p1 = new Projectile(entity, target, 54, 51, duration, 43, 31, 0, target.getSize(), 5);
            final int delay = entity.executeProjectile(p1);
            target.hit(entity, hit, delay, CombatType.MAGIC).submit();
            if (max == 65 && hit > 0) {
                player.message("You are badly burned by the dragon fire!");
            }
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return sparkAttack ? 10 : entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
