package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.damagehandler.registery.ListenerRegistry;
import com.cryptic.model.entity.player.Player;

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
                magicAccuracy.modifier += 1.45F;
                return true;
            } else if (FormulaUtils.eliteVoidEquipmentBaseMagic((Player) entity) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic((Player) entity)) {
                magicAccuracy.modifier += 1.70F;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        var attacker = (Player) entity;
        if (FormulaUtils.regularVoidEquipmentBaseMelee(attacker)) {
            meleeAccuracy.modifier += 1.10F;
            return true;
        } else if (FormulaUtils.eliteVoidEquipmentMelee(attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee(attacker)) {
            meleeAccuracy.modifier += 1.125F;
            return true;
        }
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        var attacker = (Player) entity;
        if (FormulaUtils.regularVoidEquipmentBaseRanged(attacker)) {
            rangeAccuracy.modifier += 1.10F;
            return true;
        }
        if (FormulaUtils.eliteVoidEquipmentRanged(attacker) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged(attacker)) {
            rangeAccuracy.modifier += 1.125F;
            return true;
        }
        return false;
    }
}
