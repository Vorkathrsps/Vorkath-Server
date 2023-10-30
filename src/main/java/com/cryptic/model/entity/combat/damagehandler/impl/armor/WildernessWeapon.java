package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.ItemIdentifiers;

public class WildernessWeapon implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.MAGIC) {
                    if (FormulaUtils.hasMagicWildernessWeapon(player)) {
                        if (WildernessArea.inWilderness(npc.tile())) {
                            magicAccuracy.modifier += 1.50F;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.MELEE) {
                    if (FormulaUtils.hasMeleeWildernessWeapon(player)) {
                        if (WildernessArea.inWilderness(npc.tile())) {
                            meleeAccuracy.modifier += 1.50F;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.RANGED) {
                    if (FormulaUtils.hasRangedWildernessWeapon(player)) {
                        if (WildernessArea.inWilderness(npc.tile())) {
                            rangeAccuracy.modifier += 1.50F;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
