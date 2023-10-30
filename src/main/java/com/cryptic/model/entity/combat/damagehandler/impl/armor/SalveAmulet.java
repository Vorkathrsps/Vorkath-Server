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
import com.cryptic.utility.ItemIdentifiers;

import static com.cryptic.utility.ItemIdentifiers.*;

public class SalveAmulet implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            var equipment = player.getEquipment();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.MAGIC) {
                    if (equipment.containsAny(SALVE_AMULETEI, SALVE_AMULET_E)) {
                        if (FormulaUtils.isUndead(npc)) {
                            magicAccuracy.modifier += 1.20;
                            return true;
                        }
                    } else if (equipment.contains(SALVE_AMULET)) {
                        if (FormulaUtils.isUndead(npc)) {
                            magicAccuracy.modifier += 1.15F;
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
            var equipment = player.getEquipment();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.MELEE) {
                    if (equipment.containsAny(SALVE_AMULETEI, SALVE_AMULET_E)) {
                        if (FormulaUtils.isUndead(npc)) {
                            meleeAccuracy.modifier += 1.20F;
                            return true;
                        }
                    } else if (equipment.contains(SALVE_AMULET)) {
                        if (FormulaUtils.isUndead(npc)) {
                            meleeAccuracy.modifier += 1.15F;
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
            var equipment = player.getEquipment();
            if (target instanceof NPC npc) {
                if (combatType == CombatType.RANGED) {
                    if (equipment.containsAny(SALVE_AMULETEI, SALVE_AMULET_E)) {
                        if (FormulaUtils.isUndead(npc)) {
                            rangeAccuracy.modifier += 1.20F;
                            return true;
                        }
                    } else if (equipment.contains(SALVE_AMULET)) {
                        if (FormulaUtils.isUndead(npc)) {
                            rangeAccuracy.modifier += 1.15F;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
