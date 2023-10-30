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
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        var modifier = rangeAccuracy.getModifier();
        if (entity instanceof Player player) {
            if (combatType == CombatType.RANGED) {
                if ((FormulaUtils.hasBowOfFaerdhenin(player)) || (FormulaUtils.hasCrystalBow(player))) {
                    if (player.getEquipment().contains(ItemIdentifiers.CRYSTAL_HELM) || player.getEquipment().contains(CRYSTAL_HELM_27705) || player.getEquipment().contains(CRYSTAL_HELM_27717) || player.getEquipment().contains(CRYSTAL_HELM_27729) || player.getEquipment().contains(CRYSTAL_HELM_27741) || player.getEquipment().contains(CRYSTAL_HELM_27753) || player.getEquipment().contains(CRYSTAL_HELM_27765) || player.getEquipment().contains(CRYSTAL_HELM_27777)) {
                        modifier += 1.05F;
                    }
                    if (player.getEquipment().contains(ItemIdentifiers.CRYSTAL_BODY) || player.getEquipment().contains(CRYSTAL_BODY_27697) || player.getEquipment().contains(CRYSTAL_BODY_27709) || player.getEquipment().contains(CRYSTAL_BODY_27721) || player.getEquipment().contains(CRYSTAL_BODY_27733) || player.getEquipment().contains(CRYSTAL_BODY_27745) || player.getEquipment().contains(CRYSTAL_BODY_27757) || player.getEquipment().contains(CRYSTAL_BODY_27769)) {
                        modifier += 1.15F;
                    }
                    if (player.getEquipment().contains(ItemIdentifiers.CRYSTAL_LEGS) || player.getEquipment().contains(CRYSTAL_LEGS_27701) || player.getEquipment().contains(CRYSTAL_LEGS_27713) || player.getEquipment().contains(CRYSTAL_LEGS_27725) || player.getEquipment().contains(CRYSTAL_LEGS_27737) || player.getEquipment().contains(CRYSTAL_LEGS_27749) || player.getEquipment().contains(CRYSTAL_LEGS_27761) || player.getEquipment().contains(CRYSTAL_LEGS_27773)) {
                        modifier += 1.10F;
                    }
                    rangeAccuracy.modifier += modifier;
                    return true;
                }
            }
        }
        return false;
    }

}
