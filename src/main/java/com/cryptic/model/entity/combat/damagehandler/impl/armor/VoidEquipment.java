package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.Player;

public class VoidEquipment implements DamageModifyingListener {
    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            var modifier = accuracy.modifier();
            if (combatType == CombatType.MAGIC) {
                if (FormulaUtils.regularVoidEquipmentBaseMagic(player)) {
                    modifier += 1.45F;
                    return modifier;
                } else if (FormulaUtils.eliteVoidEquipmentBaseMagic(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseMagic(player)) {
                    modifier += 1.70F;
                    return modifier;
                }
            } else {
                if (FormulaUtils.regularVoidEquipmentBaseRanged(player)) {
                    modifier += 1.10F;
                    return modifier;
                } else if (FormulaUtils.eliteVoidEquipmentRanged(player) || FormulaUtils.eliteTrimmedVoidEquipmentBaseRanged(player)) {
                    modifier += 1.125F;
                    return modifier;
                }
            }
        }
        return 0;
    }
}
