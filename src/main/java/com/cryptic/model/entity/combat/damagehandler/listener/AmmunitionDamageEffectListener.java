package com.cryptic.model.entity.combat.damagehandler.listener;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;

public interface AmmunitionDamageEffectListener {

    int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage);
}
