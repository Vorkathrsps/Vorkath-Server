package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.slayer_task.SlayerCreature;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.model.entity.attributes.AttributeKey.SLAYER_TASK_ID;

public class SlayerHelmets implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        var attacker = (Player) entity;
        var task_id = attacker.<Integer>getAttribOr(SLAYER_TASK_ID, 0);
        var task = SlayerCreature.lookup(task_id);
        if (task != null) {
            if (Slayer.creatureMatches(attacker, magicAccuracy.getDefender().getAsNpc().id())) {
                if (combatType == CombatType.MAGIC) {
                    if (attacker.getEquipment().contains(ItemIdentifiers.SLAYER_HELMET)) {
                        magicAccuracy.setModifier(1.15F);
                        return true;
                    } else if (attacker.getEquipment().contains(ItemIdentifiers.SLAYER_HELMET_I)) {
                        magicAccuracy.setModifier(1.18F);
                    } else if (attacker.getEquipment().contains(ItemIdentifiers.BLACK_SLAYER_HELMET) || attacker.getEquipment().contains(ItemIdentifiers.GREEN_SLAYER_HELMET) || attacker.getEquipment().contains(ItemIdentifiers.HYDRA_SLAYER_HELMET) || attacker.getEquipment().contains(ItemIdentifiers.PURPLE_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.RED_SLAYER_HELMET) || ((Player) attacker).getEquipment().contains(ItemIdentifiers.TURQUOISE_SLAYER_HELMET)) {
                        magicAccuracy.setModifier(1.20F);
                        return true;
                    } else if (attacker.getEquipment().contains(ItemIdentifiers.TWISTED_SLAYER_HELMET) || attacker.getEquipment().contains(ItemIdentifiers.TZKAL_SLAYER_HELMET)) {
                        magicAccuracy.setModifier(1.25F);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        return false;
    }
}
