package com.aelous.model.entity.combat.method.effects.equipment.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.combat.method.effects.registery.ListenerRegistry;
import com.aelous.model.entity.player.Player;

public class VoidEquipment implements DamageEffectListener {

    public VoidEquipment() {
        ListenerRegistry.registerListener(this);
    }
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        if (combatType == CombatType.MAGIC) {
            if (FormulaUtils.regularVoidEquipmentBaseMagic((Player) entity)) {
                magicAccuracy.setModifier(1.45F);
                return true;
            } else if (FormulaUtils.eliteVoidEquipmentBaseMagic((Player) entity) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic((Player) entity)) {
                magicAccuracy.setModifier(1.70F);
                return true;
            }
        }
        return false;
    }
}
