package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;

public class ThermonuclearSmokeDevil extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        int tileDist = entity.tile().transform(1, 1).distance(target.tile());
        new Projectile(entity, target, 644, 20, 12 * tileDist, 30, 30, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), 2, CombatType.RANGED).checkAccuracy().submit();
        target.graphic(643);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
