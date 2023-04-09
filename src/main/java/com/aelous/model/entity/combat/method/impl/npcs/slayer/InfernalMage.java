package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

/**
 * @author PVE
 * @Since augustus 07, 2020
 */
public class InfernalMage extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        entity.graphic(129, GraphicHeight.HIGH, 0);
        new Projectile(entity, target, 130, 51, entity.projectileSpeed(target), 43, 31, 0,16, 64).sendProjectile();
        int delay = entity.getProjectileHitDelay(target);
        int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);
        target.hit(entity, hit, delay, CombatType.MAGIC).checkAccuracy().submit();

        if(hit > 0) {
            target.graphic(131, GraphicHeight.HIGH, delay);
        } else {
            target.graphic(85, GraphicHeight.HIGH, delay);
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
