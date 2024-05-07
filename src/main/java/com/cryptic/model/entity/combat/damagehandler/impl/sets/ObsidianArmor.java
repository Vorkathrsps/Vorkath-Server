package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.Player;

public class ObsidianArmor implements DamageModifyingListener {
    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            if (combatType == CombatType.MELEE) {
                var modifier = accuracy.modifier();
                if (FormulaUtils.isWearingObsidianArmour(player) && FormulaUtils.hasObbyWeapon(player)) {
                    modifier += 1.10F;
                    return (int) modifier;
                }
            }
        }
        return 0;
    }
}
