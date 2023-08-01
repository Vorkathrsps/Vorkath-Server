package com.cryptic.model.entity.combat.method.impl.theatre;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

public class SotetsegCombat extends CommonCombatMethod {
    @Getter
    AtomicBoolean recentlyPerformedAttack = new AtomicBoolean(false);
    int magicAttackCount = 0;

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8))
            return false;

        var player = (Player) target;
        if (!getRecentlyPerformedAttack().get()) {
            if (magicAttackCount == 10) {
                sendSpecialMagicAttack(player);
            } else {
                if (withinDistance(1) && Utils.percentageChance(50) && !recentlyPerformedAttack.get()) {
                    sendMeleeAttack(player);
                } else {
                    sendRandomMageOrRange(player);
                }
            }
            return true;
        }
        return false;
    }

    public void sendRandomMageOrRange(Player target) {
        int[] projectileIds = new int[]{1606, 1607};
        var randomProjectile = Utils.randomElement(projectileIds);
        entity.animate(8139);
        int tileDist = entity.tile().distance(target.tile());
        int duration = (70 + 30 + (20 * tileDist));
        Projectile p = new Projectile(entity, target, randomProjectile, 70, duration, 43, 21, 25, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, randomProjectile == 1606 ? CombatType.MAGIC : CombatType.RANGED), delay, randomProjectile == 1606 ? CombatType.MAGIC : CombatType.RANGED).checkAccuracy().postDamage(d -> {
            if (randomProjectile == 1606) {
                magicAttackCount++;
            }
            if (randomProjectile == 1606 && Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MISSILES)) {
                Prayers.closeAllPrayers(target);
                target.getTimers().register(TimerKey.OVERHEADS_BLOCKED, 2);
                d.setDamage(Utils.random(1, 50));
            } else if (randomProjectile == 1607 && Prayers.usingPrayer(target, Prayers.PROTECT_FROM_MAGIC)) {
                Prayers.closeAllPrayers(target);
                target.getTimers().register(TimerKey.OVERHEADS_BLOCKED, 2);
                d.setDamage(Utils.random(1, 50));
            } else {
                if (d.getDamage() == 0) {
                    d.block();
                }
            }
        });
        hit.submit();
    }

    public void sendSpecialMagicAttack(Player target) {
        magicAttackCount = 0;
        entity.animate(8139);
        int tileDist = entity.tile().distance(target.tile());
        int duration = (70 + 25 + (25 * tileDist));
        Projectile p = new Projectile(entity, target, 1604, 70, duration, 50, 0, 50, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).setAccurate(true);
        hit.setDamage(121);
        hit.submit();
        entity.graphic(101, GraphicHeight.MIDDLE, p.getSpeed());
        recentlyPerformedAttack.getAndSet(true);
    }

    public void sendMeleeAttack(Player target) {
        entity.animate(8138);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy();
        hit.submit();
        recentlyPerformedAttack.getAndSet(true);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 14;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }
}
