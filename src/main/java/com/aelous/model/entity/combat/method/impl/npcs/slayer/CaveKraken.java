package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class CaveKraken extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        new Projectile(entity, target, 162, 32, 65, 30, 30, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.MAGIC), 1, CombatType.MAGIC).checkAccuracy().submit();
        target.graphic(163);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 15;
    }
}
