package com.cryptic.model.entity.combat.damagehandler;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;

public class PreDamageEffectHandler {
    private final DamageEffectListener ability;
    public PreDamageEffectHandler(DamageEffectListener ability) {
        this.ability = ability;
    }
    public void triggerEffectForAttacker(Entity entity, CombatType type, Hit hit) {
        ability.prepareDamageEffectForAttacker(entity, type, hit);
    }
    public int getAccuracyModification(Entity entity, CombatType type, AbstractAccuracy accuracy) {
        return ability.prepareAccuracyModification(entity, type, accuracy);
    }
}
