package com.aelous.model.content.raids.theatre.bloat.combat;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;

import javax.annotation.Nonnull;

public class BloatCombat extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(@Nonnull Entity entity, @Nonnull Entity target) {
        return true;
    }

    public void sendFlies(@Nonnull Entity entity, @Nonnull Entity target) {

    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }

    @Override
    public boolean canMultiAttackInSingleZones() {
        return super.canMultiAttackInSingleZones();
    }
}
