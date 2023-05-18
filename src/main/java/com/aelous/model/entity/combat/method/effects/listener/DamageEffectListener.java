package com.aelous.model.entity.combat.method.effects.listener;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;

public interface DamageEffectListener {
    boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit);
    boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit);
    boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy);
}
