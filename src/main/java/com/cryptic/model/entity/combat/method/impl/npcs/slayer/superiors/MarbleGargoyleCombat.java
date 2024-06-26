package com.cryptic.model.entity.combat.method.impl.npcs.slayer.superiors;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.chainedwork.Chain;
import com.cryptic.utility.timers.TimerKey;

/**
 * In combat, the marble gargoyle will occasionally launch a grey ball towards the player.
 * If hit by the projectile, it will inflict up to 38 damage and immobilise the player for a few seconds. The message box states "You have been trapped in stone!" when this occurs.
 * Players are able to avoid this attack by moving at least one tile away from their original position once the projectile is launched.
 *
 * @author Origin
 * maart 31, 2020
 */
public class MarbleGargoyleCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (Utils.random(3) == 0) {
            entity.animate(7815);
            stoneAttack(entity, target);
            entity.getTimers().register(TimerKey.COMBAT_ATTACK, 6);
        } else if (Utils.random(1) == 0 || !withinDistance(1)) {
            entity.animate(7814);
            new Projectile(entity, target, 276, 35, 70, 50, 30, 0, 10,5).sendProjectile();
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy(true).submit();
        } else {
            entity.animate(7814);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        }
        return true;
    }

    private void stoneAttack(Entity entity, Entity target) {
        Tile tile = target.tile();
        new Projectile(entity, target,1453, 30, 75, 50, 30, 0, 10,5).sendProjectile();
        Chain.bound(null).runFn(3, () -> {
            if (target.tile().equals(tile)) {
                target.hit(entity, Utils.random(38));
                target.stun(3);
                if (target instanceof Player) {
                    target.message("You have been trapped in stone!");
                }
            }
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 7;
    }
}
