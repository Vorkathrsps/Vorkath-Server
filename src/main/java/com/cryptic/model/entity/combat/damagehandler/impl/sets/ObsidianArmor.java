package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.Player;

public class ObsidianArmor implements DamageModifyingListener {
    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            if (!CombatType.MELEE.equals(combatType)) return boost;
            if (FormulaUtils.isWearingObsidianArmour(player) && FormulaUtils.hasObbyWeapon(player)) {
                boost = 1.10D;
                return boost;
            }
        }
        return boost;
    }
}
