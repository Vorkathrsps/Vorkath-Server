package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class AmuletOfAvarice implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            SlayerTask task = World.getWorld().getSlayerTasks();
            SlayerTask assignment = task.getCurrentAssignment(player);
            final Entity target = player.getCombat().getTarget();
            if (target == null) return false;
            if (target instanceof NPC npc) {
                if (!player.getEquipment().contains(ItemIdentifiers.AMULET_OF_AVARICE)) return false;
                if (!ArrayUtils.contains(FormulaUtils.isRevenant(), npc.id())) return false;
                if (((FormulaUtils.hasSlayerHelmet(player) || FormulaUtils.hasSlayerHelmetImbued(player)) && assignment != null && ArrayUtils.contains(assignment.getNpcs(), npc.id())))
                    return false;
                int damage = hit.getDamage();
                hit.setDamage((int) (damage * 1.20D));
                return true;
            }
        }
        return false;
    }

    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            SlayerTask task = World.getWorld().getSlayerTasks();
            SlayerTask assignment = task.getCurrentAssignment(player);
            final Entity target = player.getCombat().getTarget();
            if (target == null) return boost;
            if (target instanceof NPC npc) {
                if (!player.getEquipment().contains(ItemIdentifiers.AMULET_OF_AVARICE)) return boost;
                if (!ArrayUtils.contains(FormulaUtils.isRevenant(), npc.id())) return boost;
                if (((FormulaUtils.hasSlayerHelmet(player) || FormulaUtils.hasSlayerHelmetImbued(player)) && assignment != null && ArrayUtils.contains(assignment.getNpcs(), npc.id())))
                    return boost;
                boost = 1.20D;
                return boost;
            }
        }
        return boost;
    }
}
