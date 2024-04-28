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
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (CombatType.RANGED.equals(player.getCombat().getCombatType())) {
                    if (player.getEquipment().containsAny(DRAGON_HUNTER_CROSSBOW, DRAGON_HUNTER_CROSSBOW_T, DRAGON_HUNTER_CROSSBOW_B)) {
                        var modifier = accuracy.modifier();
                        if (FormulaUtils.isDragon(npc)) {
                            modifier += 1.25;
                        } else {
                            modifier += 1.30;
                        }
                        return modifier;
                    }
                }
            }
        }
        return 0;
    }
}
