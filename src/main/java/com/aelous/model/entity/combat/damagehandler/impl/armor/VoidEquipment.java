package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.damagehandler.registery.ListenerRegistry;
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

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        var attacker = (Player) entity;
        if (FormulaUtils.regularVoidEquipmentBaseMelee(attacker)) {
            meleeAccuracy.setModifier(1.10F);
            return true;
        } else if (FormulaUtils.eliteVoidEquipmentMelee(attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee(attacker)) {
            meleeAccuracy.setModifier(1.125F);
            return true;
        }
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        return false;
    }
}
