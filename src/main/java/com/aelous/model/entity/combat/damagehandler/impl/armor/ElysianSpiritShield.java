package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.damagehandler.registery.ListenerRegistry;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;
public class ElysianSpiritShield implements DamageEffectListener {
    public ElysianSpiritShield() {
        ListenerRegistry.registerListener(this);
    }
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        Player defender = (Player) entity;
        var setIgnoreElysianReduction = hit.reflected ? 1 : CombatConstants.ELYSIAN_DAMAGE_REDUCTION;
        if (Utils.percentageChance(70) && defender.getEquipment().contains(ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD)) {
            int damage = hit.getDamage();
            var reduced_value = damage - (damage * setIgnoreElysianReduction);
            hit.setDamage((int) reduced_value);
            defender.performGraphic(new Graphic(321, GraphicHeight.MIDDLE));
            return true;
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
        return false;
    }
}

