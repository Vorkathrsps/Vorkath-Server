package com.aelous.model.entity.combat.method.effects.equipment.impl.seteffects;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.container.equipment.Equipment;
import com.aelous.utility.Utils;

public class GuthanSet implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var player = (Player) entity;
        if (player.getCombat().getTarget() != null) {
            if (hit.isAccurate() && combatType == CombatType.MELEE) {
                if (FormulaUtils.wearingFullGuthan(player)) {
                    if (Utils.securedRandomChance(0.25F)) {
                        if (hit.getDamage() > 0 && !hit.reflected) {
                            player.getCombat().getTarget().graphic(398, GraphicHeight.LOW, 0);
                            player.heal(hit.getDamage(), Equipment.hasAmmyOfDamned(player) ? 10 : 0);
                            return true;
                        }
                    }
                }
            }
        }
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
        return false;
    }
}
