package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

public class VeracSet implements DamageModifyingListener {

    @Override
    public boolean isModifyAccuracy(Entity entity, AbstractAccuracy accuracy, Hit hit) {
        if (entity instanceof Player player) {
            if (player.getCombat().getTarget() != null) {
                if (hit.getCombatType() != null) {
                    if (CombatType.MELEE.equals(hit.getCombatType())) {
                        if (FormulaUtils.wearingFullVerac(player)) {
                            if (Utils.rollDie(25, 1)) {
                                int damage = hit.getDamage();
                                hit.setAccurate(true);
                                hit.setDamage(damage + 1);
                                hit.getTarget().graphic(1041);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

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
