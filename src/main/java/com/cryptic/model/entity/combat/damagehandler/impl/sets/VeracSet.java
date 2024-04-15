package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

public class VeracSet implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            if (player.getCombat().getTarget() != null) {
                if (CombatType.MELEE.equals(combatType)) {
                    if (FormulaUtils.wearingFullVerac(player)) {
                        if (Utils.rollDie(25, 1)) {
                            hit.ignorePrayer();
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
