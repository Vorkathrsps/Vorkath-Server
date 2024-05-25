package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

public class AhrimSet implements DamageModifyingListener {

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            if (player.getCombat().getTarget() != null) {
                if (CombatType.MAGIC.equals(combatType)) {
                    if (FormulaUtils.wearingFullAhrims(player) && FormulaUtils.wearingAmuletOfDamned(player)) {
                        if (Utils.rollDie(25, 1)) {
                            final Entity target = hit.getTarget();
                            if (target != null) {
                                hit.setDamage((int) (hit.getDamage() * 1.30));
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
