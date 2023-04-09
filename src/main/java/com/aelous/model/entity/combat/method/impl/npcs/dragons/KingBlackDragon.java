package com.aelous.model.entity.combat.method.impl.npcs.dragons;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Utils;

import java.util.Arrays;

public class KingBlackDragon extends CommonCombatMethod {

    private enum FireType {
        FIRE, FREEZE, SHOCK, POISON
    }

    private static final int[] SHOCK_STATS = {
        Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC
    };

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && Utils.rollDie(4, 1))
            basicAttack(entity, target);
        else {
            if (Utils.rollDie(2, 1))
                fire(entity, target, FireType.FIRE, 0);
            else switch (Utils.random(3)) {
                case 1 -> {
                    fire(entity, target, FireType.FREEZE, 10);
                    if (Utils.rollDie(3, 1))
                        target.freeze(3, entity);
                }
                case 2 -> {
                    fire(entity, target, FireType.SHOCK, 12);
                    if (target != null && Utils.rollDie(3, 1))
                        Arrays.stream(SHOCK_STATS).forEach(skill -> target.getSkills().alterSkill(skill, -2));
                }
                case 3 -> {
                    fire(entity, target, FireType.POISON, 10);
                    if (Utils.rollDie(3, 1))
                        target.poison(8);
                }
            }
        }
        return true;
    }

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        entity.animate(entity.attackAnimation());
    }

    private void fire(Entity entity, Entity target, FireType fireType, int minMaxDamage) {
        entity.animate(81);
        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));

        if (target instanceof Player player) {
            double max = Math.max(65, minMaxDamage);
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
            if (max == 65 && hit > 0) {
                player.message("You are badly burned by the dragon fire!");
            }
            switch (fireType) {
                case FIRE -> {
                    Projectile p1 = new Projectile(entity, target, 393, 51, duration, 43, 31, 0, target.getSize(), 5);
                    final int delay = entity.executeProjectile(p1);
                    target.hit(entity, hit, delay, CombatType.MAGIC).submit();
                }

                case POISON -> {
                    Projectile p2 = new Projectile(entity, target, 394, 51, duration, 43, 31, 0, target.getSize(), 5);
                    final int delay = entity.executeProjectile(p2);
                    target.hit(entity, hit, delay, CombatType.MAGIC).submit();
                }
                case SHOCK -> {
                    Projectile p3 = new Projectile(entity, target, 395, 51, duration, 43, 31, 0, target.getSize(), 5);
                    final int delay = entity.executeProjectile(p3);
                    target.hit(entity, hit, delay, CombatType.MAGIC).submit();
                }
                case FREEZE -> {
                    Projectile p4 = new Projectile(entity, target, 396, 51, duration, 43, 31, 0, target.getSize(), 5);
                    final int delay = entity.executeProjectile(p4);
                    target.hit(entity, hit, delay, CombatType.MAGIC).submit();
                }
            }
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }

}
