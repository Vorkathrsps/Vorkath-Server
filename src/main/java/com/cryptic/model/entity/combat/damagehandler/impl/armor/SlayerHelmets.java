package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

import static com.cryptic.utility.ItemIdentifiers.*;

public class SlayerHelmets implements DamageModifyingListener {
    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            SlayerTask slayerTask = World.getWorld().getSlayerTasks();
            var assignment = slayerTask.getCurrentAssignment(player);
            if (target instanceof NPC npc) {
                if (assignment != null) {
                    if (ArrayUtils.contains(assignment.getNpcs(), npc.id())) {
                        if (equipment.contains(SLAYER_HELMET)) {
                            boost = 1.15D;
                            return boost;
                        } else if (equipment.contains(SLAYER_HELMET_I)) {
                            boost = 1.18D;
                            return boost;
                        } else if (equipment.contains(BLACK_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.RED_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                            boost = 1.20D;
                            return boost;
                        } else if (equipment.contains(TWISTED_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                            boost = 1.25D;
                            return boost;
                        }
                    }
                }
            }
        }
        return boost;
    }
}
