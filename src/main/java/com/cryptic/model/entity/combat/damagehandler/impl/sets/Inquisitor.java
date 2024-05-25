package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.weapon.AttackType;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.*;

public class Inquisitor implements DamageModifyingListener {
    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            if (player.getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
                if (player.getEquipment().containsAll(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT)) {
                    boost = 1.01D;
                    return boost;
                }
                if (player.getEquipment().hasAt(EquipSlot.HEAD, INQUISITORS_GREAT_HELM)) {
                    boost += 0.005D;
                }
                if (player.getEquipment().hasAt(EquipSlot.BODY, INQUISITORS_HAUBERK)) {
                    boost = 0.005D;
                }
                if (player.getEquipment().hasAt(EquipSlot.LEGS, INQUISITORS_PLATESKIRT)) {
                    boost = 0.005D;
                }
                boost += 1.0D;
                return boost;
            }
        }
        return boost;
    }
}
