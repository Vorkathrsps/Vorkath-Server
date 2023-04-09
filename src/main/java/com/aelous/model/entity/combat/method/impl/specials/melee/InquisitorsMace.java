package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.utility.chainedwork.Chain;

public class InquisitorsMace extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1060);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy();
        hit.submit();

        for (int index = 0; index < 6; index++) {
            Chain.bound(null).name("ele_bow_freeze_effect").cancelWhen(() -> {
                return !entity.tile().isWithinDistance(target.tile()) || target.dead(); // cancels as expected
            }).runFn(index * 4, () -> {
                Hit bleed = target.hit(entity, World.getWorld().random(1, 5),4, CombatType.MELEE).checkAccuracy();
                bleed.submit();
            });
        }
        CombatSpecial.drain(entity, CombatSpecial.INQUISITORS_MACE.getDrainAmount());
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
