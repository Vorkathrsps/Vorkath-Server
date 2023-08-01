package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.damagehandler.registery.ListenerRegistry;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
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

