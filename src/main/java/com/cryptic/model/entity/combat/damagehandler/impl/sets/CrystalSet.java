package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

import static com.cryptic.utility.ItemIdentifiers.*;

public class CrystalSet implements DamageModifyingListener {
    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            if (CombatType.RANGED.equals(combatType)) {
                if ((FormulaUtils.hasBowOfFaerdhenin(player) || FormulaUtils.hasCrystalBow(player))) {
                    if (player.getEquipment().contains(ItemIdentifiers.CRYSTAL_HELM) || player.getEquipment().contains(CRYSTAL_HELM_27705) || player.getEquipment().contains(CRYSTAL_HELM_27717) || player.getEquipment().contains(CRYSTAL_HELM_27729) || player.getEquipment().contains(CRYSTAL_HELM_27741) || player.getEquipment().contains(CRYSTAL_HELM_27753) || player.getEquipment().contains(CRYSTAL_HELM_27765) || player.getEquipment().contains(CRYSTAL_HELM_27777)) {
                        boost += 0.05D;
                    }
                    if (player.getEquipment().contains(ItemIdentifiers.CRYSTAL_BODY) || player.getEquipment().contains(CRYSTAL_BODY_27697) || player.getEquipment().contains(CRYSTAL_BODY_27709) || player.getEquipment().contains(CRYSTAL_BODY_27721) || player.getEquipment().contains(CRYSTAL_BODY_27733) || player.getEquipment().contains(CRYSTAL_BODY_27745) || player.getEquipment().contains(CRYSTAL_BODY_27757) || player.getEquipment().contains(CRYSTAL_BODY_27769)) {
                        boost += 0.15D;
                    }
                    if (player.getEquipment().contains(ItemIdentifiers.CRYSTAL_LEGS) || player.getEquipment().contains(CRYSTAL_LEGS_27701) || player.getEquipment().contains(CRYSTAL_LEGS_27713) || player.getEquipment().contains(CRYSTAL_LEGS_27725) || player.getEquipment().contains(CRYSTAL_LEGS_27737) || player.getEquipment().contains(CRYSTAL_LEGS_27749) || player.getEquipment().contains(CRYSTAL_LEGS_27761) || player.getEquipment().contains(CRYSTAL_LEGS_27773)) {
                        boost += 0.10D;
                    }
                    boost += 1.0D;
                    return boost;
                }
            }
        }
        return boost;
    }
}
