package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;

public class ZurielStaff implements DamageModifyingListener {
    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            if (combatType == CombatType.MAGIC) {
                var modifier = accuracy.modifier();
                if (player.getSpellbook().equals(MagicSpellbook.ANCIENTS) && FormulaUtils.hasZurielStaff(player)) {
                    modifier += 1.10F;
                    return modifier;
                }
            }
        }
        return 0;
    }
}
