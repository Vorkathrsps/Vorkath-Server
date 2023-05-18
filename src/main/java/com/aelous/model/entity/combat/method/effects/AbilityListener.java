package com.aelous.model.entity.combat.method.effects;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;

public interface AbilityListener {
    boolean prepareEffect(Entity entity, CombatType combatType, Hit hit);
}
