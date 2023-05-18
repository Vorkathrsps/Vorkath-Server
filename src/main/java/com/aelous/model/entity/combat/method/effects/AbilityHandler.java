package com.aelous.model.entity.combat.method.effects;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;

public class AbilityHandler {
    private final AbilityListener ability;
    public AbilityHandler(AbilityListener ability) {
        this.ability = ability;
    }
    public void triggerEffect(Entity entity, CombatType type, Hit hit) {
        ability.prepareEffect(entity, type, hit);
    }
}
