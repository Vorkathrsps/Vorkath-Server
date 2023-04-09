package com.aelous.model.entity.combat.method.impl.npcs.bosses.vorkath;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;

public class ZombifiedSpawn extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}
