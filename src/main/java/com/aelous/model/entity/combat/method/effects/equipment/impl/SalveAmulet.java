package com.aelous.model.entity.combat.method.effects.equipment.impl;

import com.aelous.cache.definitions.identifiers.NpcIdentifiers;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.FormulaUtils;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.effects.listener.DamageEffectListener;
import com.aelous.model.entity.combat.method.effects.registery.ListenerRegistry;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;

import static com.aelous.utility.ItemIdentifiers.SALVE_AMULET_E;

public class SalveAmulet implements DamageEffectListener {

    public SalveAmulet() {
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
        if (attacker.getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI) || attacker.getAsPlayer().getEquipment().contains(SALVE_AMULET_E) || attacker.getAsPlayer().getEquipment().contains(ItemIdentifiers.SALVE_AMULETEI)) {
            if (magicAccuracy.getDefender().isNpc() && FormulaUtils.isUndead(magicAccuracy.getDefender().getAsNpc())) {
                magicAccuracy.setModifier(1.20F);
                return true;
            }
        } else if (attacker.getEquipment().contains(ItemIdentifiers.SALVE_AMULET) && magicAccuracy.getDefender().isNpc() && FormulaUtils.isUndead(magicAccuracy.getDefender().getAsNpc())) {
            magicAccuracy.setModifier(1.15F);
            return true;
        }
        return false;
    }
}
