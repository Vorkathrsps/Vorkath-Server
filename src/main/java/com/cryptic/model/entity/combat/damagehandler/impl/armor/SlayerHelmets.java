package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

import static com.cryptic.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;
import static com.cryptic.utility.ItemIdentifiers.*;

public class SlayerHelmets implements DamageEffectListener {
    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            var task_id = player.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
            var task = SlayerCreature.lookup(task_id);
            if (target instanceof NPC npc) {
                if (task != null) {
                    if (Slayer.creatureMatches(player, npc.id())) {
                        var modifier = accuracy.modifier();
                            if (equipment.contains(SLAYER_HELMET)) {
                                modifier += 1.15F;
                                return modifier;
                            } else if (equipment.contains(SLAYER_HELMET_I)) {
                                modifier += 1.18F;
                                return modifier;
                            } else if (equipment.contains(BLACK_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.RED_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                                modifier += 1.20F;
                                return modifier;
                            } else if (equipment.contains(TWISTED_SLAYER_HELMET) || equipment.contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                                modifier += 1.25F;
                                return modifier;
                            }
                    }
                }
            }
        }
        return 0;
    }
}
