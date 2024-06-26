package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

public class DinhBulwark implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var damage = hit.getDamage();
        if (entity instanceof Player player) {
            if (!player.getEquipment().contains(ItemIdentifiers.DINHS_BULWARK)) return false;
            if (player.getCombat().getFightType().getChildId() != 2) return false;
            if (damage > 0) {
                damage -= damage / 5;
                damage = (int) Math.floor(damage * CombatConstants.DINHS_BULWARK_REDUCTION);
                hit.setDamage(damage);
                return true;
            }
        }
        return false;
    }
}
