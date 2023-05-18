package com.aelous.model.entity.combat.method.effects.equipment.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.combat.method.effects.registery.ListenerRegistry;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Color;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

public class BrimstoneRing implements DamageEffectListener {

    public BrimstoneRing() {
        ListenerRegistry.registerListener(this);
    }

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
            if (attacker.getEquipment().contains(ItemIdentifiers.BRIMSTONE_RING)) {
                if (Utils.securedRandomChance(0.25F)) {
                    attacker.message(Color.RED.wrap("Your attack ignored 10% of your opponent's magic defence."));
                    magicAccuracy.setModifier(1.10F);//1.10
                    return true;
                }
            }
        }
        return false;
    }
}
