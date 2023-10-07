package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;

import static com.cryptic.utility.ItemIdentifiers.TWISTED_BOW;

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
                    rangeAccuracy.modifier += bonus;
                }
            }
            return true;
        }
        return false;
    }
}
