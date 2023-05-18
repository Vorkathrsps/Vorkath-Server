package com.aelous.model.entity.combat.method.effects.equipment.impl.seteffects;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.combat.method.effects.registery.ListenerRegistry;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.Utils;

public class KarilSet implements DamageEffectListener {

    public KarilSet() {
        ListenerRegistry.registerListener(this);
    }
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var player = (Player) entity;
        var getTarget = player.getCombat().getTarget();
        if (hit.isAccurate() && combatType == CombatType.RANGED) {
            if (FormulaUtils.wearingFullKarils(player)) {
                if (Utils.securedRandomChance(0.25F)) {
                    if (getTarget.getSkills().level(Skills.AGILITY) > 20) {
                        getTarget.graphic(401, GraphicHeight.HIGH, 0);
                        getTarget.getSkills().setLevel(Skills.AGILITY, getTarget.getSkills().level(Skills.AGILITY) - 20);
                        return true;
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
