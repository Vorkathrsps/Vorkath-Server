package com.cryptic.model.entity.combat.damagehandler.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.impl.bolts.*;
import com.cryptic.model.entity.combat.damagehandler.listener.AmmunitionDamageEffectListener;

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
        listeners.add(new DragonStoneBolts());
        listeners.add(new EmeraldBolts());
        listeners.add(new JadeBolt());
        listeners.add(new OnyxBolts());
        listeners.add(new OpalBolts());
        listeners.add(new PearlBolts());
        listeners.add(new RubyBolts());
        listeners.add(new SapphireBolts());
        listeners.add(new TopazBolts());
        return listeners;
    }

    @Override
    public int prepareBoltSpecialEffect(Entity player, Entity target, CombatType combatType, int damage) {
        for (var listener : ammunitionDamageListeners) {
            int modifiedDamage = listener.prepareBoltSpecialEffect(player, target, combatType, damage);
            if (modifiedDamage > 0) {
                return modifiedDamage;
            }
        }
        return damage;
    }

    public AmmunitionDamageEffect() {

    }
}
