package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.*;

public class DragonHunterCrossbow implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (player.getCombat().getCombatType() == CombatType.RANGED) {
                if (target instanceof NPC npc) {
                    if (player.getEquipment().containsAny(DRAGON_HUNTER_CROSSBOW, DRAGON_HUNTER_CROSSBOW_T, DRAGON_HUNTER_CROSSBOW_B)) {
                        if (combatType == CombatType.MELEE) {
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
        }
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
            if (target instanceof NPC npc) {
                if (combatType == CombatType.RANGED) {
                    if (player.getEquipment().containsAny(DRAGON_HUNTER_CROSSBOW, DRAGON_HUNTER_CROSSBOW_T, DRAGON_HUNTER_CROSSBOW_B)) {
                        if (FormulaUtils.isDragon(npc)) {
                            rangeAccuracy.modifier += 1.25F;
                        } else {
                            rangeAccuracy.modifier += 1.30F;
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
