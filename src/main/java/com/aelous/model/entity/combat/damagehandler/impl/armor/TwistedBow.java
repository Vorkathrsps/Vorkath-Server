package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;

import java.util.stream.Stream;

import static com.aelous.utility.ItemIdentifiers.TWISTED_BOW;

public class TwistedBow implements DamageEffectListener {
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
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        if (entity.getCombat().getTarget() == null) {
            return false;
        }
        if (!entity.getCombat().getTarget().isNpc()) {
            return false;
        }
        var attacker = (Player) entity;
        var target = rangeAccuracy.getDefender().getAsNpc();
        if (attacker.isPlayer() && target.isNpc()) {
            if (attacker.getEquipment().contains(TWISTED_BOW)) {
                float bonus = 1;
                int magicLevel;
                if (target.getCombatInfo() != null && target.getCombatInfo().stats != null) {
                    magicLevel = target.getCombatInfo().stats.magic > 350 && attacker.raidsParty != null ? 350 : Math.min(target.getCombatInfo().stats.magic, 250);
                } else {
                    magicLevel = target.getSkills().getMaxLevel(Skills.MAGIC);
                }

                bonus += 140.0f + (((10.0f * 3.0f * magicLevel) / 10.0f) - 10.0f) - ((float) Math.floor(3.0f * magicLevel / 10.0f - 100.0f) * 2.0f);
                bonus = (float) Math.floor(bonus / 100);

                if (bonus > 2.4)
                    bonus = 2.4f;

                if (attacker.isPlayer() && target.isNpc()) {
                    rangeAccuracy.setModifier(bonus);
                }
            }
            return true;
        }
        return false;
    }
}
