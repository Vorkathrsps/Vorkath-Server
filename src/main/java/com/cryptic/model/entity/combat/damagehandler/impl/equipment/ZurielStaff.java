package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.MagicSpellbook;
import com.cryptic.model.entity.player.Player;

public class ZurielStaff implements DamageModifyingListener {
    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            if (!FormulaUtils.hasZurielStaff(player)) return boost;
            if (!CombatType.MAGIC.equals(combatType)) return boost;
            if (!player.getSpellbook().equals(MagicSpellbook.ANCIENTS)) return boost;
            boost = 1.10D;
            return boost;
        }
        return boost;
    }
}
