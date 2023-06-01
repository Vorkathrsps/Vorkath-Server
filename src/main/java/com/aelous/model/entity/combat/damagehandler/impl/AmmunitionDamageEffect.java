package com.aelous.model.entity.combat.damagehandler.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.impl.bolts.DiamondBolts;
import com.aelous.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;

import java.util.ArrayList;
import java.util.List;

public class AmmunitionDamageEffect implements AmmunitionDamageEffectListener {

    private static final List<AmmunitionDamageEffectListener> ammunitionDamageListeners;

    static {
        ammunitionDamageListeners = initializeAmmunitionDamageListeners();
    }

    private static List<AmmunitionDamageEffectListener> initializeAmmunitionDamageListeners() {
        List<AmmunitionDamageEffectListener> listeners = new ArrayList<>();
        listeners.add(new DiamondBolts());
        return listeners;
    }

    @Override
    public int prepareBoltSpecialEffect(Entity player, Entity target, CombatType combatType, int damage) {
        for (var listener : ammunitionDamageListeners) {
            if (damage > 0) {
                return listener.prepareBoltSpecialEffect(player, target, combatType, damage);
            }
        }
        return damage;
    }
}
