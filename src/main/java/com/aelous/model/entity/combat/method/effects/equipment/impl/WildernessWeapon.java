package com.aelous.model.entity.combat.method.effects.equipment.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.position.areas.impl.WildernessArea;

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
        if (combatType == CombatType.MAGIC) {
            if (FormulaUtils.hasMagicWildernessWeapon(attacker) && magicAccuracy.getDefender().isNpc() && WildernessArea.inWilderness(magicAccuracy.getDefender().getAsNpc().tile())) {
                magicAccuracy.setModifier(1.50F);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean prepareMeleeAccuracyModification(Entity entity, CombatType combatType, MeleeAccuracy meleeAccuracy) {
        var attacker = (Player) entity;
        if (FormulaUtils.hasMeleeWildernessWeapon(attacker) && meleeAccuracy.getDefender().isNpc() && WildernessArea.inWilderness(meleeAccuracy.getDefender().getAsNpc().tile())) {
            meleeAccuracy.setModifier(1.50F);
            return true;
        }
        return false;
    }

    @Override
    public boolean prepareRangeAccuracyModification(Entity entity, CombatType combatType, RangeAccuracy rangeAccuracy) {
        return false;
    }
}
