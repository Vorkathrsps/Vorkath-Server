package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class CrystalHalberd extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1203);
        entity.graphic(1235, GraphicHeight.HIGH, 0);
        if (target.getSize() == 1) {
             entity.submitHit(target, 1, this);
        } else {
            for (int index = 0; index < 2; index++) {
                 entity.submitHit(target, 1, this);
            }
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
