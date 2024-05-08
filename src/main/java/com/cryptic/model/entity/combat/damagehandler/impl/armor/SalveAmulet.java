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
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            if (target instanceof NPC npc) {
                if (!FormulaUtils.isUndead(npc)) return boost;
                if (equipment.containsAny(SALVE_AMULET_E) && !CombatType.RANGED.equals(player.getCombat().getCombatType()) && !CombatType.MAGIC.equals(player.getCombat().getCombatType())) {
                    boost = 1.20D;
                    return boost;
                }
                if (equipment.containsAny(SALVE_AMULETEI, SALVE_AMULETEI_25278, SALVE_AMULETEI_26782)) {
                    boost = 1.20D;
                    return boost;
                } else if (equipment.containsAny(SALVE_AMULETI, SALVE_AMULETI_25250, SALVE_AMULETI_26763)) {
                    if (CombatType.MAGIC.equals(player.getCombat().getCombatType())) {
                        boost = 1.15D;
                        return boost;
                    }
                    boost = 1.167D;
                    return boost;
                } else if (equipment.contains(SALVE_AMULET) && !CombatType.RANGED.equals(player.getCombat().getCombatType()) && !CombatType.MAGIC.equals(player.getCombat().getCombatType())) {
                    boost = 1.15D;
                    return boost;
                }
            }
        }
        return boost;
    }
}
