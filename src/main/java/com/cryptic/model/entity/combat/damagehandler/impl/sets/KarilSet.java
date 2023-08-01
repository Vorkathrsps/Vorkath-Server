package com.cryptic.model.entity.combat.damagehandler.impl.sets;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.FormulaUtils;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.damagehandler.registery.ListenerRegistry;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;

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
