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
            final Entity target = player.getCombat().getTarget();
            if (target == null) return boost;
            if (!(target instanceof NPC)) return boost;
            if (!WildernessArea.inWilderness(target.tile())) return boost;
            if (CombatType.MAGIC.equals(combatType)) {
                if (!FormulaUtils.hasMagicWildernessWeapon(player)) return boost;
                boost = 1.50D;
                return boost;
            } else if (CombatType.RANGED.equals(combatType)) {
                if (!FormulaUtils.hasRangedWildernessWeapon(player)) return boost;
                boost = 1.50D;
                return boost;
            } else if (CombatType.MELEE.equals(combatType)) {
                if (!FormulaUtils.hasMeleeWildernessWeapon(player)) return boost;
                boost = 1.50D;
                return boost;
            }
        }
        return boost;
    }
}
