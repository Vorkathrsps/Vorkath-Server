package com.cryptic.model.entity.combat.method.impl.npcs.dragons;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;

import java.util.Arrays;

public class KingBlackDragonCombat extends CommonCombatMethod {

    private enum FireType {
        FIRE, FREEZE, SHOCK, POISON
    }

    private static final int[] SHOCK_STATS = {
        Skills.ATTACK, Skills.STRENGTH, Skills.DEFENCE, Skills.RANGED, Skills.MAGIC
    };

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }
        if (isReachable()) {
            basicAttack(entity, target);
            return true;
        }
        var random = World.getWorld().random().nextInt(0, 7);
        switch (random) {
            case 0, 1 -> {
                if (Utils.rollDie(3, 1)) target.freeze(3, entity, true);
                fire(entity, target, FireType.FREEZE, 0);
            }
            case 2, 3 -> {
                if (Utils.rollDie(3, 1)) Arrays.stream(SHOCK_STATS).forEach(skill -> target.getSkills().alterSkill(skill, -2));
                fire(entity, target, FireType.SHOCK, 12);
            }
            case 4, 5 -> {
                if (Utils.rollDie(3, 1)) target.poison(8);
                fire(entity, target, FireType.POISON, 10);
            }
            case 6, 7 -> fire(entity, target, FireType.FIRE, 0);
            case 8, 9 -> {
                if (isReachable()) basicAttack(entity, target);
            }
        }
        return true;
    }

    private void basicAttack(Entity entity, Entity target) {
        System.out.println("basic");
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    private void fire(Entity entity, Entity target, FireType fireType, int minMaxDamage) {
        entity.animate(81);
        var tileDist = entity.getCentrePosition().transform(3, 3, 0).getManHattanDist(entity.tile(), target.tile());
        int duration = (41 + 11 + (5 * tileDist));

        if (target instanceof Player player) {
            double max = Math.max(65, minMaxDamage);
            int antifire_charges = player.getAttribOr(AttributeKey.ANTIFIRE_POTION, 0);
            boolean hasShield = CombatConstants.hasAntiFireShield(player);
            boolean hasPotion = antifire_charges > 0;


            if (player.<Boolean>getAttribOr(AttributeKey.SUPER_ANTIFIRE_POTION, false)) {
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
                    Projectile p1 = new Projectile(entity, target, 393, 51, duration, 43, 31, 0, entity.getSize(), 5);
                    final int delay = entity.executeProjectile(p1);
                    new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
                }

                case POISON -> {
                    Projectile p2 = new Projectile(entity, target, 394, 51, duration, 43, 31, 0, entity.getSize(), 5);
                    final int delay = entity.executeProjectile(p2);
                    new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
                }
                case SHOCK -> {
                    Projectile p3 = new Projectile(entity, target, 395, 51, duration, 43, 31, 0, entity.getSize(), 5);
                    final int delay = entity.executeProjectile(p3);
                    new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
                }
                case FREEZE -> {
                    Projectile p4 = new Projectile(entity, target, 396, 51, duration, 43, 31, 0, entity.getSize(), 5);
                    final int delay = entity.executeProjectile(p4);
                    new Hit(entity, target, delay, CombatType.MAGIC).checkAccuracy(true).submit();
                }
            }
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }

}
