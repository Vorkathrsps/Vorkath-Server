package com.cryptic.model.entity.combat.damagehandler;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;

public class DamageModifyingHandler {
    private final DamageModifyingListener ability;
    public DamageModifyingHandler(DamageModifyingListener ability) {
        this.ability = ability;
    }
    public void applyModifiedAccuracy(Entity player, AbstractAccuracy accuracy, Hit hit) {
        ability.isModifyAccuracy(player, accuracy, hit);
    }
    public void triggerEffectForAttacker(Entity entity, CombatType type, Hit hit) {
        ability.prepareDamageEffectForAttacker(entity, type, hit);
    }
    public int getAccuracyModification(Entity entity, CombatType type, AbstractAccuracy accuracy) {
        return ability.prepareAccuracyModification(entity, type, accuracy);
    }
}
