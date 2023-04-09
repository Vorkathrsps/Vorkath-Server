package com.aelous.model.entity.combat.method.impl.npcs.slayer.superiors;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

/**
 * The greater abyssal demon has a special attack, like other high levelled superior slayer monsters.
 * The greater abyssal demon will teleport around the player, hitting them quickly at an attack speed of 1 for the next four hits (one hit per teleport).
 * This special attack has 100% accuracy regardless of the player's defensive bonuses and is almost always guaranteed to hit 20 for each attack,
 * so it is advised to keep Protect from Melee active at all times while killing the greater abyssal demon.
 * <p>
 * Like all demons, the greater abyssal demon is weak against demonbane weapons.
 *
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * maart 31, 2020
 */
public class GreaterAbyssalDemon extends CommonCombatMethod {

    private static final byte[][] BASIC_OFFSETS = new byte[][]{{0, -1}, {-1, 0}, {0, 1}, {1, 0}};

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!entity.isNpc() || !target.isPlayer())
            return false;
        entity.animate(entity.attackAnimation());
        if (Utils.percentageChance(20)) {
            teleportAttack(entity, target);
            entity.getTimers().register(TimerKey.COMBAT_ATTACK, 17);//Set attack timer to 17 ticks, because that's how long this attack lasts for.
        } else {
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy().submit();
            if (Utils.random(4) == 0) {
                final byte[] offsets = BASIC_OFFSETS[Utils.random(3)];
                target.teleport(target.getX() + offsets[0], target.getY() + offsets[1],target.getZ());
            }
        }
        return true;
    }

    private void teleportAttack(Entity entity, Entity target) {

        final byte[] offsets = BASIC_OFFSETS[Utils.random(3)];

        if (entity.dead() || target.dead() || !target.tile().isWithinDistance(entity.tile(), 15)) {
            return;
        }

        Chain.bound(null).runFn(2, () -> {
            entity.teleport(new Tile(target.getX() + offsets[0], target.getY() + offsets[1], entity.getZ()));
        }).then(2, () -> { // First attack
            entity.graphic(409);
            entity.setPositionToFace(target.tile());
            entity.animate(entity.attackAnimation());
            target.hit(entity, Utils.random(31), CombatType.MELEE).checkAccuracy().submit();
        }).then(2, () -> {
            entity.teleport(new Tile(target.getX() + offsets[0], target.getY() + offsets[1], entity.getZ()));
        }).then(2, () -> {// Second attack
            entity.graphic(409);
            entity.setPositionToFace(target.tile());
            entity.animate(entity.attackAnimation());
            target.hit(entity, Utils.random(31), CombatType.MELEE).checkAccuracy().submit();
        }).then(2, () -> {
            entity.teleport(new Tile(target.getX() + offsets[0], target.getY() + offsets[1], entity.getZ()));
        }).then(2, () -> { // Third attack
            entity.graphic(409);
            entity.setPositionToFace(target.tile());
            entity.animate(entity.attackAnimation());
            target.hit(entity, Utils.random(31), CombatType.MELEE).checkAccuracy().submit();
        }).then(2, () -> {
            entity.teleport(new Tile(target.getX() + offsets[0], target.getY() + offsets[1], entity.getZ()));
        }).then(2, () -> {// Fourth attack
            entity.graphic(409);
            entity.setPositionToFace(target.tile());
            entity.animate(entity.attackAnimation());
            target.hit(entity, Utils.random(31), CombatType.MELEE).checkAccuracy().submit();
        });
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 2;
    }
}
