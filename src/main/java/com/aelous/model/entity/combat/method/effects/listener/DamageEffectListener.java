package com.aelous.model.entity.combat.method.effects.listener;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;

public interface DamageEffectListener {
    boolean prepareEffectForAttacker(Entity entity, CombatType combatType, Hit hit);
    boolean prepareEffectForDefender(Entity entity, CombatType combatType, Hit hit);
}
