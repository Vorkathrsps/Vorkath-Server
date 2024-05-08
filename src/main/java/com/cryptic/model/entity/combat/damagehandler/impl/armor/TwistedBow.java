package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.cryptic.utility.ItemIdentifiers.TWISTED_BOW;

public class TwistedBow implements DamageModifyingListener {
    private static final Logger logger = LogManager.getLogger(TwistedBow.class);

    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            final Entity target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            if (target == null) return boost;
            if (target instanceof NPC npc) {
                if (!CombatType.RANGED.equals(combatType)) return boost;
                if (!equipment.contains(TWISTED_BOW)) return boost;
                int magicLevel;
                if (npc.getCombatInfo() != null) {
                    if (npc.getCombatInfo().stats != null) {
                        magicLevel = npc.getCombatInfo().stats.magic > 350 && player.raidsParty != null ? 350 : Math.min(npc.getCombatInfo().stats.magic, 250);
                    } else {
                        magicLevel = npc.getSkills().getMaxLevel(Skills.MAGIC);
                    }

                    boost += 140.0f + (((10.0f * 3.0f * magicLevel) / 10.0f) - 10.0f) - ((float) Math.floor(3.0f * magicLevel / 10.0f - 100.0f) * 2.0f);
                    boost = (float) Math.floor(boost / 100);

                    if (boost > 2.4F) {
                        boost = 2.4F;
                    }
                    return boost;
                }
            }
        }
        return boost;
    }
}
