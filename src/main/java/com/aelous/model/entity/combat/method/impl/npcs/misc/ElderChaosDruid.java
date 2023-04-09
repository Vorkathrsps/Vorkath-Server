package com.aelous.model.entity.combat.method.impl.npcs.misc;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.map.position.Tile;
import com.aelous.utility.Utils;
import com.aelous.utility.timers.TimerKey;

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
            new Projectile(entity, target, 159, 51, 60, 43, 31, 0).sendProjectile();
            target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy().submit();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 5;
    }
}
