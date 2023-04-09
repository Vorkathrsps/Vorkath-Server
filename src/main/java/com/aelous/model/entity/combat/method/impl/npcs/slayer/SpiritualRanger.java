package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
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
            NPC npc = (NPC) entity;
            var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());
            var delay = Math.max(1, (20 + (tileDist * 12)) / 30);

            new Projectile(entity, target, 1192, getDelay(npc.id()), 5 * npc.tile().distance(target.tile()), getProjectileHeight(npc.id()), 33, 0).sendProjectile();

            int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED);
            target.hit(entity, hit,1, CombatType.RANGED).checkAccuracy().submit();
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
