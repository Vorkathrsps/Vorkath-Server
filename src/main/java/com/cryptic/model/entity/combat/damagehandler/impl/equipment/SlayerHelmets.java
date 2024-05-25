package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
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
            final Entity target = player.getCombat().getTarget();
            final SlayerTask slayerTask = World.getWorld().getSlayerTasks();
            final SlayerTask assignment = slayerTask.getCurrentAssignment(player);
            if (target instanceof NPC npc) {
                if (assignment != null) {
                    if (ArrayUtils.contains(assignment.getNpcs(), npc.id())) {
                        if (FormulaUtils.hasSlayerHelmet(player)) {
                            boost = 1.15D;
                            return boost;
                        }
                        if (FormulaUtils.hasSlayerHelmetImbued(player)) {
                            boost = 1.20D;
                            return boost;
                        }
                    }
                }
            }
        }
        return boost;
    }
}
