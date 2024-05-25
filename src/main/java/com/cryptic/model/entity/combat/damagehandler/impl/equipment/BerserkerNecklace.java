package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

public class BerserkerNecklace implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            if (combatType == null) return false;
            if (!CombatType.MELEE.equals(combatType)) return false;
            if (!player.getEquipment().containsAny(ItemIdentifiers.BERSERKER_NECKLACE, ItemIdentifiers.BERSERKER_NECKLACE_OR)) return false;
            if (!FormulaUtils.hasObbyWeapon(player)) return false;
            int damage = hit.getDamage();
            hit.setDamage((int) (damage * 1.05D));
            return true;
        }
        return false;
    }
}
