package com.cryptic.model.entity.combat.method.impl.npcs.misc;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.map.position.Tile;
import com.cryptic.utility.Utils;
import com.cryptic.utility.timers.TimerKey;

public class ElderChaosDruid extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        //Check to see if we're able to teleport the player beside us.
        if (Utils.rollDie(5, 1) && entity.tile().distance(target.tile()) > 3 && entity.tile().distance(target.tile()) < 6 &&
            !target.getTimers().has(TimerKey.ELDER_CHAOS_DRUID_TELEPORT)) {
            target.getTimers().addOrSet(TimerKey.ELDER_CHAOS_DRUID_TELEPORT, 5);
            target.teleport(new Tile(entity.tile().getX(), entity.tile().getY() - 1));
            target.graphic(409);
            entity.forceChat("You dare run from us!");
        } else {
            entity.animate(entity.attackAnimation());
            entity.graphic(158);
            int tileDist = entity.tile().distance(target.tile());
            int duration = (51 + -5 + (10 * tileDist));
            Projectile p = new Projectile(entity, target, 159, 51, duration, 43, 31, 0, entity.getSize(), 10);
            final int delay = entity.executeProjectile(p);
            Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
            hit.submit();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 5;
    }
}
