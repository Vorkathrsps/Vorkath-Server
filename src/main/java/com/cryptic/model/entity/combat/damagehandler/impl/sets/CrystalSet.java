package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

import static com.cryptic.utility.ItemIdentifiers.*;

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
        var modifier = rangeAccuracy.getModifier();

        if ((FormulaUtils.hasBowOfFaerdhenin(attacker))) {
            if (attacker.getEquipment().contains(ItemIdentifiers.CRYSTAL_HELM) || attacker.getEquipment().contains(CRYSTAL_HELM_27705) || attacker.getEquipment().contains(CRYSTAL_HELM_27717) || attacker.getEquipment().contains(CRYSTAL_HELM_27729) || attacker.getEquipment().contains(CRYSTAL_HELM_27741) || attacker.getEquipment().contains(CRYSTAL_HELM_27753) || ((Player) attacker).getEquipment().contains(CRYSTAL_HELM_27765) || attacker.getEquipment().contains(CRYSTAL_HELM_27777)) {
                modifier += 1.05F;
            }
            if (attacker.getEquipment().contains(ItemIdentifiers.CRYSTAL_BODY) || attacker.getEquipment().contains(CRYSTAL_BODY_27697) || attacker.getEquipment().contains(CRYSTAL_BODY_27709) || attacker.getEquipment().contains(CRYSTAL_BODY_27721) || attacker.getEquipment().contains(CRYSTAL_BODY_27733) || attacker.getEquipment().contains(CRYSTAL_BODY_27745) || ((Player) attacker).getEquipment().contains(CRYSTAL_BODY_27757) || attacker.getEquipment().contains(CRYSTAL_BODY_27769)) {
                modifier += 1.15F;
            }
            if (attacker.getEquipment().contains(ItemIdentifiers.CRYSTAL_LEGS) || attacker.getEquipment().contains(CRYSTAL_LEGS_27701) || attacker.getEquipment().contains(CRYSTAL_LEGS_27713) || attacker.getEquipment().contains(CRYSTAL_LEGS_27725) || attacker.getEquipment().contains(CRYSTAL_LEGS_27737) || attacker.getEquipment().contains(CRYSTAL_LEGS_27749) || ((Player) attacker).getEquipment().contains(CRYSTAL_LEGS_27761) || attacker.getEquipment().contains(CRYSTAL_LEGS_27773)) {
                modifier += 1.10F;
            }
            rangeAccuracy.modifier += modifier;
            return true;
        }

        return false;
    }

}
