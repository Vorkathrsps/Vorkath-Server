package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;

public class DinhBulwark implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var damage = hit.getDamage();
        if (entity instanceof Player player) {
            if (player.getEquipment().contains(ItemIdentifiers.DINHS_BULWARK) && player.getCombat().getFightType().getChildId() == 2) {
                if (damage > 0) {
                    damage -= damage / 5;
                    damage = (int) Math.floor(damage * CombatConstants.DINHS_BULWARK_REDUCTION);
                    hit.setDamage(damage);
                    return true;
                }
            }
        }
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
        return false;
    }
}
