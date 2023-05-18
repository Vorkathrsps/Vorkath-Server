package com.aelous.model.entity.combat.method.effects.equipment.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.registery.ListenerRegistry;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
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
        if (Utils.securedRandomChance(0.7F) && defender.getEquipment().contains(ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD)) {
            int damage = hit.getDamage();
            damage = (int) Math.floor(damage * CombatConstants.ELYSIAN_DAMAGE_REDUCTION);
            hit.setDamage(damage);
            defender.performGraphic(new Graphic(321, GraphicHeight.MIDDLE));
            entity.message("bs");
            return true;
        }
        return false;
    }

    @Override
    public boolean prepareMagicAccuracyModification(Entity entity, CombatType combatType, MagicAccuracy magicAccuracy) {
        return false;
    }
}

