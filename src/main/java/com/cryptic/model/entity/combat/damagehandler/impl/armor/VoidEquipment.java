package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.player.Player;

public class VoidEquipment implements DamageEffectListener {

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
        if (entity instanceof Player player) {
            if (combatType == CombatType.MAGIC) {
                if (FormulaUtils.regularVoidEquipmentBaseMagic(player)) {
                    magicAccuracy.modifier += 1.45F;
                    return true;
                } else if (FormulaUtils.eliteVoidEquipmentBaseMagic(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic(player)) {
                    magicAccuracy.modifier += 1.70F;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        if (entity instanceof Player player) {
            if (combatType == CombatType.MELEE) {
                if (FormulaUtils.regularVoidEquipmentBaseMelee(player)) {
                    meleeAccuracy.modifier += 1.10F;
                    return true;
                } else if (FormulaUtils.eliteVoidEquipmentMelee(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMelee(player)) {
                    meleeAccuracy.modifier += 1.125F;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        if (entity instanceof Player player) {
            if (combatType == CombatType.RANGED) {
                if (FormulaUtils.regularVoidEquipmentBaseRanged(player)) {
                    rangeAccuracy.modifier += 1.10F;
                    return true;
                } else if (FormulaUtils.eliteVoidEquipmentRanged(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged(player)) {
                    rangeAccuracy.modifier += 1.125F;
                    return true;
                }
            }
        }
        return false;
    }

}
