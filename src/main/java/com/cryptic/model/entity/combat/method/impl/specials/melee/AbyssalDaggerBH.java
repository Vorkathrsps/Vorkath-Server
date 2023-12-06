package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class AbyssalDaggerBH extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(3300);
        entity.graphic(1283, GraphicHeight.LOW, 0);
        var hit = entity.submitHit(target, 0, this);
        entity.submitHit(target, 0, this);
        entity.sendPublicSound(2537, hit.getDelay());
        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_DAGGER_BH.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed() + 1;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
