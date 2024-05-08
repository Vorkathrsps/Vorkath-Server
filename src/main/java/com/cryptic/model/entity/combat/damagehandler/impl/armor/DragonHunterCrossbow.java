package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.*;

public class DragonHunterCrossbow implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (CombatType.RANGED.equals(player.getCombat().getCombatType())) {
                if (target instanceof NPC npc) {
                    if (player.getEquipment().containsAny(DRAGON_HUNTER_CROSSBOW, DRAGON_HUNTER_CROSSBOW_T, DRAGON_HUNTER_CROSSBOW_B)) {
                        if (FormulaUtils.isDragon(npc)) {
                            var damage = hit.getDamage();
                            var increase = 1.20;
                            var output = damage * increase;
                            hit.setDamage((int) output);
                            return true;
                        }
                    }
                }
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
                if (!player.getEquipment().containsAny(DRAGON_HUNTER_CROSSBOW, DRAGON_HUNTER_CROSSBOW_T, DRAGON_HUNTER_CROSSBOW_B)) return boost;
                if (!CombatType.RANGED.equals(player.getCombat().getCombatType())) return boost;
                if (FormulaUtils.isDragon(npc)) {
                    boost = 1.25D;
                } else {
                    boost = 1.30D;
                }
                return boost;
            }
        }
        return boost;
    }
}
