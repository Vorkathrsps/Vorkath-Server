package com.aelous.model.entity.combat.method.effects.equipment;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.combat.method.effects.equipment.impl.AmuletOfBloodFury;
import com.aelous.model.entity.combat.method.effects.equipment.impl.ElysianSpiritShield;
import com.aelous.model.entity.combat.method.effects.equipment.impl.ToxicStaffOfTheDead;

import java.util.ArrayList;
import java.util.List;

public class EquipmentDamageEffect implements DamageEffectListener {
    private final List<DamageEffectListener> damageEffectListenersAttacker;
    private final List<DamageEffectListener> damageEffectListenersDefender;

    public EquipmentDamageEffect() {
        damageEffectListenersAttacker = new ArrayList<>();
        damageEffectListenersDefender = new ArrayList<>();
        damageEffectListenersAttacker.add(new AmuletOfBloodFury());

        damageEffectListenersAttacker.add(new ToxicStaffOfTheDead());
        damageEffectListenersDefender.add(new ElysianSpiritShield());
    }

    @Override
    public boolean prepareEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        for (DamageEffectListener listener : damageEffectListenersAttacker) {
            if (listener.prepareEffectForAttacker(entity, combatType, hit)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        for (DamageEffectListener listener : damageEffectListenersDefender) {
            if (listener.prepareEffectForDefender(entity, combatType, hit)) {
                return true;
            }
        }
        return false;
    }
}
