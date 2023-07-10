package com.aelous.model.entity.combat.method.impl.npcs.barrows;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.utility.Utils;

public class Dharok extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        if (!withinDistance(1)) {
            return false;
        }

        entity.animate(entity.attackAnimation());

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy();

        if (Utils.rollDie(4)) {
            hit.damageModifier((int) (entity.getAsNpc().maxHp() - entity.getAsNpc().hp() * 0.01));
        } else {
            hit.submit();
        }
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
