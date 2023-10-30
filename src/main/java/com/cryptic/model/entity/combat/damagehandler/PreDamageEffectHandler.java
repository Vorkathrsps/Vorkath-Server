package com.cryptic.model.entity.combat.damagehandler;

import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PreDamageEffectHandler {
    private final DamageEffectListener ability;
    public PreDamageEffectHandler(DamageEffectListener ability) {
        this.ability = ability;
    }
    public void triggerEffectForAttacker(Entity entity, CombatType type, Hit hit) {
        ability.prepareDamageEffectForAttacker(entity, type, hit);
    }
    public void triggerMagicAccuracyModificationAttacker(Entity entity, CombatType type, MagicAccuracy magicAccuracy) {
        ability.prepareMagicAccuracyModification(entity, type, magicAccuracy);
    }
    public void triggerRangeAccuracyModificationAttacker(Entity entity, CombatType type, RangeAccuracy rangeAccuracy) {
        ability.prepareRangeAccuracyModification(entity, type, rangeAccuracy);
    }
    public void triggerMeleeAccuracyModificationAttacker(Entity entity, CombatType type, MeleeAccuracy meleeAccuracy) {
        ability.prepareMeleeAccuracyModification(entity, type, meleeAccuracy);
    }
}
