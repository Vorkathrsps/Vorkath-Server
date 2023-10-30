package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.PreDamageEffectHandler;
import com.cryptic.model.entity.combat.damagehandler.impl.EquipmentDamageEffect;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.weapon.AttackType;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

import static com.cryptic.utility.ItemIdentifiers.*;

public class Inquisitor implements DamageEffectListener {
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
        if (entity instanceof Player player) {
            var modifier = meleeAccuracy.getModifier();
            if (player.getCombat().getFightType().getAttackType() == AttackType.CRUSH) {
                if (player.getEquipment().containsAll(INQUISITORS_GREAT_HELM, INQUISITORS_HAUBERK, INQUISITORS_PLATESKIRT)) {
                    meleeAccuracy.modifier += 1.01F;
                    return true; //return early if wearing all
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
                meleeAccuracy.modifier += modifier;
                return true; //return late if returning individual pieces
            }
        }
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        return false;
    }
}
