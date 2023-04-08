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
    }

    private void sparkAttack(NPC npc, Entity target) {
        npc.animate(81);
        for (int i = 0; i < 2; i++) {
            Tile pos = target.tile().relative(Utils.get(-1, 1), Utils.get(-1, 1));
            NPC spark = new NPC(8032, pos);

            //When projectile hits ground sparks start following player
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

            //new Projectile(npc.tile(), pos, 0, 1488, 160, 40,10, 0, 0, 16, 192).sendProjectile();
        }
    }


    private void doMelee(NPC npc, Entity target) {
        npc.animate(npc.attackAnimation());
        target.hit(npc, Utils.random(npc.getCombatInfo().maxhit), CombatType.MELEE).checkAccuracy().submit();
    }

    private void doRangedAttack(NPC npc, Entity target) {
        npc.animate(81);
        new Projectile(npc, target, 1486, 40, npc.projectileSpeed(target), 10, 31, 0, 16, 127).sendProjectile();
        int damage = Utils.random(npc.getCombatInfo().maxhit);
        //The second attack is a ranged attack that hits through Protect from Missiles, and uses the Life Leech effect from enchanted onyx bolts,
        // where the dragon will heal itself for 100% of the damage dealt to the player.
        if (Utils.rollDie(5, 2)) {
            target.hit(npc, damage, npc.getProjectileHitDelay(target), CombatType.RANGED).submit();
            npc.heal(damage, npc.maxHp());
        } else {
            //Regular ranged attack
            target.hit(npc, damage, npc.getProjectileHitDelay(target), CombatType.RANGED).checkAccuracy().submit();
        }
    }

    private void doMagicBlast(NPC npc, Entity target) {
        npc.animate(81);
        new Projectile(npc, target, 162, 40, npc.projectileSpeed(target), 10, 31, 0, 16, 127).sendProjectile();
        target.hit(npc, Utils.random(npc.getCombatInfo().maxhit), npc.getProjectileHitDelay(target), CombatType.MAGIC).checkAccuracy().submit();
        //target.delayedGraphics(new Graphic(163, GraphicHeight.HIGH), npc.getProjectileHitDelay(target));
    }

    private void doDragonBreath(NPC npc, Entity target) {
        npc.animate(81);
        new Projectile(npc, target, 54, 50, npc.projectileSpeed(target), 22, 32, 0, 5, 24).sendProjectile();
        if(target instanceof Player) {
            Player player = (Player) target;
            double max = 50.0;
            int antifire_charges = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire_charges > 0;

            boolean memberEffect = player.getMemberRights().isExtremeMemberOrGreater(player) && !WildernessArea.inWild(player);
            if (max > 0 && player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false) || memberEffect) {
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

            int hit = Utils.random((int) max);
            player.hit(npc, hit, npc.getProjectileHitDelay(player), CombatType.MAGIC).submit();
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
