package com.aelous.model.entity.combat.method.impl.npcs.slayer.superiors;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;

/**
 * they will only use their Magic attack if attacked first (which will always be the case since they are not aggressive) or from afar.
 * <p>
 * The night beast has a special attack where it will briefly stop attacking then it uses Magic for three attacks which resemble Fire Blast,
 * covering a 3x3 area dealing damage based on the player's current hitpoints. It is best to have Protect from Melee active and take the hits,
 * as the attack will never kill the player. The attack can be avoided by simply moving away from the targeted area.
 *
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * maart 31, 2020
 */
public class NightBeast extends CommonCombatMethod {

    private Tile groupAttackTile;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        if (Utils.random(3) == 0) {
            sendGroupMagicAttack(entity, target);
            entity.getTimers().register(TimerKey.COMBAT_ATTACK, 6); //Cooldown
        } else if (Utils.random(1) == 0 || !CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target)) {
            new Projectile(entity, target, 130, 35, 70, 5, 38, 0, 10,5).sendProjectile();
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy().submit();
            entity.getTimers().register(TimerKey.COMBAT_ATTACK, 2); //Cooldown
        } else {
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
        }
        return true;
    }

    private void sendGroupMagicAttack(Entity entity, Entity target) {
        Tile tile = target.tile();

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                groupAttackTile = tile.transform(x, y, 0);
                new Projectile(entity, target, 130, 35, 70, 5, 38, 0, 10, 5).sendProjectile();
                //We need to send a couple of projectiles around the player this just sends one.
                //TODO need to ask Jaks help this attack is as following: https://oldschool.runescape.wiki/images/1/16/Night_beast_AoE_attack.png?6f6b8
            }
        }
        if (target.tile().isWithinDistance(groupAttackTile, 1)) {
            target.hit(entity, CombatFactory.calcDamageFromType(entity,target,CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy().submit();
            target.graphic(131);
        }
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
