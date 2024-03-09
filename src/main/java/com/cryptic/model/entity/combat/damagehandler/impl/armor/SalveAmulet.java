package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.*;

public class SalveAmulet implements DamageEffectListener {
    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            if (target instanceof NPC npc) {
                var modifier = accuracy.modifier();
                if (equipment.containsAny(SALVE_AMULETEI, SALVE_AMULET_E)) {
                    if (FormulaUtils.isUndead(npc)) {
                        modifier += 1.20;
                        return modifier;
                    }
                } else if (equipment.contains(SALVE_AMULET)) {
                    if (FormulaUtils.isUndead(npc)) {
                        modifier += 1.15F;
                        return modifier;
                    }
                }
            }
        }
        return 0;
    }
}
