package com.aelous.model.entity.combat.damagehandler;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;
import com.aelous.model.entity.combat.hit.Hit;

public class PreAmmunitionDamageEffectHandler {

    private final AmmunitionDamageEffectListener ammunitionDamage;

    public PreAmmunitionDamageEffectHandler(AmmunitionDamageEffectListener ammunitionDamage) {
        this.ammunitionDamage = ammunitionDamage;
    }

    public int triggerAmmunitionDamageModification(Entity entity, Entity target, CombatType combatType, int damage) {
       return ammunitionDamage.prepareBoltSpecialEffect(entity, target, combatType, damage);
    }
}
