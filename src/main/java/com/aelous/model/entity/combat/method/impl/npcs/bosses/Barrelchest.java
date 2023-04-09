package com.aelous.model.entity.combat.method.impl.npcs.bosses;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;

public class Barrelchest extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        meleeAttack(entity, target);
        return true;
    }

    private void meleeAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy().submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}
