package com.cryptic.model.entity.combat.damagehandler.listener;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;

public interface DamageEffectListener {
    default boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    default int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        return 0;
    }
}
