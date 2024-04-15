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
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            var modifier = accuracy.modifier();
            if (player.getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
                if (player.getEquipment().containsAll(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT)) {
                    modifier += 1.01F;
                    return modifier;
                }
                if (player.getEquipment().hasAt(EquipSlot.HEAD, INQUISITORS_GREAT_HELM)) {
                    modifier += 1.005F;
                }
                if (player.getEquipment().hasAt(EquipSlot.BODY, INQUISITORS_HAUBERK)) {
                    modifier += 1.005F;
                }
                if (player.getEquipment().hasAt(EquipSlot.LEGS, INQUISITORS_PLATESKIRT)) {
                    modifier += 1.005F;
                }
                modifier += modifier;
                return modifier;
            }
        }
        return 0;
    }
}
