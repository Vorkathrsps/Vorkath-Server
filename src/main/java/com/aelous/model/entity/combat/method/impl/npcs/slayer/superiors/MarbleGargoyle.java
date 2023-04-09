package com.aelous.model.entity.combat.method.impl.npcs.slayer.superiors;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

/**
 * In combat, the marble gargoyle will occasionally launch a grey ball towards the player.
 * If hit by the projectile, it will inflict up to 38 damage and immobilise the player for a few seconds. The message box states "You have been trapped in stone!" when this occurs.
 * Players are able to avoid this attack by moving at least one tile away from their original position once the projectile is launched.
 *
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * maart 31, 2020
 */
public class MarbleGargoyle extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (Utils.random(3) == 0) {
            entity.animate(7815);
            stoneAttack(entity, target);
            entity.getTimers().register(TimerKey.COMBAT_ATTACK, 6);
        } else if (Utils.random(1) == 0 || !CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            entity.animate(7814);
            new Projectile(entity, target, 276, 35, 70, 50, 30, 0, 10,5).sendProjectile();
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy().submit();
        } else {
            entity.animate(7814);
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
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
    public int getAttackDistance(Entity entity) {
        return 7;
    }
}
