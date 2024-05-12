package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

public class Arclight implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            final Entity target = hit.getTarget();
            if (combatType == null) return false;
            if (!CombatType.MELEE.equals(combatType)) return false;
            if (!player.getEquipment().containsAny(ItemIdentifiers.ARCLIGHT)) return false;
            if (target == null) return false;
            if (target instanceof NPC npc) {
                if (!FormulaUtils.isDemon(npc)) return false;
                double boost = 1.70D;
                final int damage = hit.getDamage();
                boost = getBoost(hit, boost);
                hit.setDamage((int) (damage * boost));
                return true;
            }
        }
        return false;
    }

    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            final Entity target = player.getCombat().getTarget();
            if (target == null) return boost;
            if (target instanceof NPC npc) {
                if (!player.getEquipment().contains(ItemIdentifiers.ARCLIGHT)) return boost;
                if (!CombatType.MELEE.equals(combatType)) return boost;
                if (!FormulaUtils.isDemon(target)) return boost;
                if (npc.id() == 12191) boost = 1.50D;
                else boost = 1.70D;
                return boost;
            }
        }
        return boost;
    }

    private static double getBoost(Hit hit, double boost) {
        if (hit.getTarget() != null) {
            if (hit.getTarget() instanceof NPC npc) {
                if (npc.id() == 12191) {
                    boost = 1.50D;
                }
            }
        }
        return boost;
    }
}
