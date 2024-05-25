package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

public class VestasLongsword extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(7515);
        entity.submitHit(target, 1, this);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
