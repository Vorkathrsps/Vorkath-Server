package com.aelous.model.entity.combat.damagehandler.impl.sets;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.utility.ItemIdentifiers.*;

public class CrystalSet implements DamageEffectListener {
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
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        var attacker = (Player) entity;
        if ((FormulaUtils.hasBowOfFaerdhenin(attacker))) {
            if (attacker.getEquipment().contains(ItemIdentifiers.CRYSTAL_HELM) || attacker.getEquipment().contains(CRYSTAL_HELM_27705)  || ((Player) attacker).getEquipment().contains(CRYSTAL_HELM_27717) || attacker.getEquipment().contains(CRYSTAL_HELM_27729) || attacker.getEquipment().contains(CRYSTAL_HELM_27741) || attacker.getEquipment().contains(CRYSTAL_HELM_27753) || ((Player) attacker).getEquipment().contains(CRYSTAL_HELM_27765) || attacker.getEquipment().contains(CRYSTAL_HELM_27777)) {
                rangeAccuracy.setModifier(1.05F);
                return true;
            }
            if (attacker.getEquipment().contains(ItemIdentifiers.CRYSTAL_BODY) || attacker.getEquipment().contains(CRYSTAL_BODY_27697)  || ((Player) attacker).getEquipment().contains(CRYSTAL_BODY_27709) || attacker.getEquipment().contains(CRYSTAL_BODY_27721) || attacker.getEquipment().contains(CRYSTAL_BODY_27733) || attacker.getEquipment().contains(CRYSTAL_BODY_27745) || ((Player) attacker).getEquipment().contains(CRYSTAL_BODY_27757) || attacker.getEquipment().contains(CRYSTAL_BODY_27769)) {
                rangeAccuracy.setModifier(1.15F);
                return true;
            }
            if (attacker.getEquipment().contains(ItemIdentifiers.CRYSTAL_LEGS) || attacker.getEquipment().contains(CRYSTAL_LEGS_27701)  || ((Player) attacker).getEquipment().contains(CRYSTAL_LEGS_27713) || attacker.getEquipment().contains(CRYSTAL_LEGS_27725) || attacker.getEquipment().contains(CRYSTAL_LEGS_27737) || attacker.getEquipment().contains(CRYSTAL_LEGS_27749) || ((Player) attacker).getEquipment().contains(CRYSTAL_LEGS_27761) || attacker.getEquipment().contains(CRYSTAL_LEGS_27773)) {
                rangeAccuracy.setModifier(1.10F);
                return true;
            }
            return true;
        }
        return false;
    }
}
