package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.World;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public class AbyssalTentacle extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1658);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        target.graphic(341, GraphicHeight.HIGH, 0);
        target.freeze(8, entity);// 5 second freeze timer
        if (World.getWorld().rollDie(100, 25)) {
            target.poison(4);
        }
        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_TENTACLE.getDrainAmount());
        return true;
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
