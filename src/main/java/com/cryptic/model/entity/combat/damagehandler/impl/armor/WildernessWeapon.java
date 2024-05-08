package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

public class WildernessWeapon implements DamageModifyingListener {
    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.MAGIC) {
                    if (FormulaUtils.hasMagicWildernessWeapon(player) || FormulaUtils.hasMeleeWildernessWeapon(player) || FormulaUtils.hasRangedWildernessWeapon(player)) {
                        if (WildernessArea.inWilderness(npc.tile())) {
                            boost = 1.50D;
                            return boost;
                        }
                    }
                }
            }
        }
        return boost;
    }
}
