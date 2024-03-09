package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.Equipment;
import com.cryptic.utility.Utils;

public class GuthanSet implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            if (player.getCombat().getTarget() != null) {
                if (combatType == CombatType.MELEE) {
                    if (FormulaUtils.wearingFullGuthan(player)) {
                        if (hit.isAccurate()) {
                            if (hit.getDamage() > 0 && !hit.reflected) {
                                if (Utils.rollDie(25, 1)) {
                                    player.getCombat().getTarget().graphic(398, GraphicHeight.LOW, 0);
                                    player.heal(hit.getDamage(), Equipment.hasAmmyOfDamned(player) ? 10 : 0);
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
