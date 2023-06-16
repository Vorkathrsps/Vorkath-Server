package com.aelous.model.entity.combat.method.impl.theatre;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.route.routes.ProjectileRoute;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SotetsegCombat extends CommonCombatMethod {

    int magicAttackCount = 0;


    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8))
            return false;
        if (magicAttackCount == 10) {
            sendSpecialMagicAttack(target);
        } else {
            if (CombatFactory.canReach(entity, CombatFactory.MELEE_COMBAT, target) && Utils.percentageChance(50)) {
                sendMeleeAttack(target);
            } else {
                sendRandomMageOrRange(target);
            }
        }
        return true;
    }

    public void sendRandomMageOrRange(Entity target) {
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

    public void sendSpecialMagicAttack(Entity target) {
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
    }

    public void sendMeleeAttack(Entity target) {
        entity.animate(8138);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy();
        hit.submit();
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 14;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return true;
    }
}
