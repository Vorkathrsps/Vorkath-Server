package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 3;
    }
}
