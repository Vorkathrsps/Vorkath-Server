package com.cryptic.model.entity.combat.damagehandler.listener;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;

public interface DamageModifyingListener {
    default boolean isModifyAccuracy(Entity player, AbstractAccuracy accuracy, Hit hit) {
        return false;
    }
    default boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }
    default double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        return 1.0D;
    }
}
