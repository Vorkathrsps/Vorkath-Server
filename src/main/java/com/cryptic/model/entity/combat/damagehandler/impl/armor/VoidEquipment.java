package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.Player;

public class VoidEquipment implements DamageModifyingListener {
    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            if (combatType == CombatType.MAGIC) {
                if (FormulaUtils.regularVoidEquipmentBaseMagic(player)) {
                    boost = 1.45D;
                    return boost;
                } else if (FormulaUtils.eliteVoidEquipmentBaseMagic(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic(player)) {
                    boost = 1.70D;
                    return boost;
                }
            } else {
                if (FormulaUtils.regularVoidEquipmentBaseRanged(player)) {
                    boost = 1.10D;
                    return boost;
                } else if (FormulaUtils.eliteVoidEquipmentRanged(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged(player)) {
                    boost = 1.125D;
                    return boost;
                }
            }
        }
        return boost;
    }
}
