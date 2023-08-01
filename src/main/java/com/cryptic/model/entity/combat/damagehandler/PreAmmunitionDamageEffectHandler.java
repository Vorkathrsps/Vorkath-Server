package com.cryptic.model.entity.combat.damagehandler;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;

public class PreAmmunitionDamageEffectHandler {

    private final AmmunitionDamageEffectListener ammunitionDamage;

    public PreAmmunitionDamageEffectHandler(AmmunitionDamageEffectListener ammunitionDamage) {
        this.ammunitionDamage = ammunitionDamage;
    }

    public int triggerAmmunitionDamageModification(Entity entity, Entity target, CombatType combatType, int damage) {
       return ammunitionDamage.prepareBoltSpecialEffect(entity, target, combatType, damage);
    }
}
