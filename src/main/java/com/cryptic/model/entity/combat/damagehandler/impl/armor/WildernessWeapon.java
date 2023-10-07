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

public class WildernessWeapon implements DamageEffectListener {
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
        if (FormulaUtils.hasMagicWildernessWeapon(attacker)) {
            if (attacker.getCombat().getTarget() instanceof NPC npc) {
                if (WildernessArea.inWilderness(npc.tile())) {
                    if (FormulaUtils.hasMagicWildernessWeapon(attacker) && magicAccuracy.getDefender().isNpc() && WildernessArea.inWilderness(magicAccuracy.getDefender().getAsNpc().tile())) {
                        magicAccuracy.modifier += 1.50F;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        var attacker = (Player) entity;
        if (FormulaUtils.hasMeleeWildernessWeapon(attacker)) {
            if (attacker.getCombat().getTarget() instanceof NPC npc) {
                if (WildernessArea.inWilderness(npc.tile())) {
                    meleeAccuracy.modifier += 1.50F;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        var attacker = (Player) entity;
        if (FormulaUtils.hasRangedWildernessWeapon(attacker)) {
            if (attacker.getCombat().getTarget() instanceof NPC npc) {
                if (WildernessArea.inWilderness(npc.tile())) {
                    rangeAccuracy.modifier += 1.50F;
                    return true;
                }
            }
        }
        return false;
    }
}
