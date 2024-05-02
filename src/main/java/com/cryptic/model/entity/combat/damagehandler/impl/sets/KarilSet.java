package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;

import java.util.concurrent.atomic.AtomicReference;

public class KarilSet implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            if (player.getCombat().getTarget() != null) {
                if (CombatType.RANGED.equals(combatType)) {
                    if (FormulaUtils.wearingFullKarils(player)) {
                        if (hit.isAccurate()) {
                            if (Utils.rollDie(25, 1)) {
                                Entity target = hit.getTarget();
                                if (target != null) {
                                    if (player.getCombat().getTarget().getSkills() != null) {
                                        if (player.getCombat().getTarget().getSkills().level(Skills.AGILITY) > 20) {
                                            player.getCombat().getTarget().graphic(401, GraphicHeight.HIGH, 0);
                                            player.getCombat().getTarget().getSkills().alterSkill(Skills.AGILITY, player.getCombat().getTarget().getSkills().level(Skills.AGILITY) - 20);
                                            return true;
                                        }
                                    }
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
