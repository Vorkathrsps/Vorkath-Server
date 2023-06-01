package com.aelous.model.entity.combat.damagehandler.listener;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;

public interface AmmunitionDamageEffectListener {

    int prepareBoltSpecialEffect(Entity entity, Entity target, CombatType combatType, int damage);
}
