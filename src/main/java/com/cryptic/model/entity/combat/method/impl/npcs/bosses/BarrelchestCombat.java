package com.cryptic.model.entity.combat.method.impl.npcs.bosses;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

public class BarrelchestCombat extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        meleeAttack(entity, target);
        return true;
    }

    private void meleeAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) return;
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }

    @Override
    public void doFollowLogic() {
        follow(1);
    }
}
