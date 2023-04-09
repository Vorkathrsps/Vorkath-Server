package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public class CrystalHalberd extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1203);
        entity.graphic(1235, GraphicHeight.HIGH, 0);

        int h1 = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
        int h2 = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);

        if(target.getSize() == 1) {
            Hit hit = target.hit(entity, h1,1, CombatType.MELEE).checkAccuracy();
            hit.submit();
        } else {
            Hit hit = target.hit(entity, h1,1, CombatType.MELEE).checkAccuracy();
            hit.submit();
            Hit hit2 = target.hit(entity, h2,1, CombatType.MELEE).checkAccuracy();
            hit2.submit();
        }
        CombatSpecial.drain(entity, CombatSpecial.CRYSTAL_HALBERD.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 3;
    }
}
