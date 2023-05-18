package com.aelous.model.entity.combat.method.effects;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;

public class PreDamageEffectHandler {
    private final DamageEffectListener ability;
    public PreDamageEffectHandler(DamageEffectListener ability) {
        this.ability = ability;
    }
    public void triggerEffectForAttacker(Entity entity, CombatType type, Hit hit) {
        ability.prepareEffectForAttacker(entity, type, hit);
    }

    public void triggerEffectForDefender(Entity entity, CombatType type, Hit hit) {
        ability.prepareEffectForDefender(entity, type, hit);
    }
}
