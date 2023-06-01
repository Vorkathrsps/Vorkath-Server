package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.npc.NPC;

public class SpiritualRanger extends CommonCombatMethod {

    private int getProjectileHeight(int npc) {
        return npc == 2211 ? 45 : 30;
    }

    private int getDelay(int npc) {
        return npc == 2242 ? 20 : 40;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (entity.isNpc()) {
            entity.animate(entity.attackAnimation());
            int tileDist = entity.tile().distance(target.tile());
            int duration = (41 + -5 + (10 * tileDist));
            var tile = entity.tile().translateAndCenterLargeNpc(entity, target);
            Projectile p = new Projectile(tile, target, 1192, 41, duration, 43, 31, 0, entity.getSize(), 10);
            final int delay = entity.executeProjectile(p);
            Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.MAGIC).checkAccuracy();
            hit.submit();
        }
        return true;
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
