package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.cryptic.utility.ItemIdentifiers.TWISTED_BOW;

public class TwistedBow implements DamageEffectListener {
    private static final Logger logger = LogManager.getLogger(TwistedBow.class);
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
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.RANGED) {
                    if (equipment.contains(TWISTED_BOW)) {
                        int magicLevel;
                        float bonus = 1F;
                        if (npc.getCombatInfo() != null) {
                            if (npc.getCombatInfo().stats != null) {
                                magicLevel = npc.getCombatInfo().stats.magic > 350 && player.raidsParty != null ? 350 : Math.min(npc.getCombatInfo().stats.magic, 250);
                            } else {
                                magicLevel = npc.getSkills().getMaxLevel(Skills.MAGIC);
                            }

                            bonus += 140.0f + (((10.0f * 3.0f * magicLevel) / 10.0f) - 10.0f) - ((float) Math.floor(3.0f * magicLevel / 10.0f - 100.0f) * 2.0f);
                            bonus = (float) Math.floor(bonus / 100);

                            if (bonus > 2.4F) {
                                bonus = 2.4F;
                            }

                            rangeAccuracy.modifier += bonus;
                            return true;
                        } else {
                            logger.log(Level.WARN, "[DamageHandler][" + getClass().getName() + "]" + " NPC CombatInfo Null For: ["  + npc.getMobName() + "] ID: [" + npc.getId() + "]");
                        }
                    }
                }
            }
        }
        return false;
    }
}
