package com.aelous.model.entity.combat.method.effects.equipment;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.AbilityListener;
import com.aelous.model.entity.combat.method.effects.equipment.impl.AmuletOfBloodFury;
import com.aelous.model.entity.combat.method.effects.equipment.impl.ToxicStaffOfTheDead;

import java.util.ArrayList;
import java.util.List;

public class EquipmentAbility implements AbilityListener {
    private final List<AbilityListener> abilityListeners;

    public EquipmentAbility() {
        abilityListeners = new ArrayList<>();
        abilityListeners.add(new AmuletOfBloodFury());
        abilityListeners.add(new ToxicStaffOfTheDead());
    }

    @Override
    public boolean prepareEffect(Entity entity, CombatType combatType, Hit hit) {
        for (AbilityListener listener : abilityListeners) {
            if (listener.prepareEffect(entity, combatType, hit)) {
                return true;
            }
        }
        return false;
    }
}
