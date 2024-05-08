package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.*;

public class SalveAmulet implements DamageModifyingListener {
    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            if (target instanceof NPC npc) {
                int boost;
                if (equipment.containsAny(SALVE_AMULETEI, SALVE_AMULET_E, SALVE_AMULETEI_25278, SALVE_AMULETEI_26782, SALVE_AMULETI_25250, SALVE_AMULETI_26763)) {
                    if (FormulaUtils.isUndead(npc)) {
                        boost = 20;
                        return boost;
                    }
                } else if (equipment.contains(SALVE_AMULET)) {
                    if (FormulaUtils.isUndead(npc)) {
                        boost = 15;
                        return boost;
                    }
                }
            }
        }
        return 0;
    }
}
