package com.cryptic.model.entity.combat.damagehandler.impl.equipment;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.container.equipment.Equipment;

import static com.cryptic.utility.ItemIdentifiers.VESTAS_BLIGHTED_LONGSWORD;

public class VestaLongsword implements DamageModifyingListener {

    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            final Equipment equipment = player.getEquipment();
            if (!equipment.contains(VESTAS_BLIGHTED_LONGSWORD)) return boost;
            if (!CombatType.MELEE.equals(combatType)) return boost;
            if (player.isSpecialActivated()) {
                boost = 1.25D;
                return boost;
            }
        }
        return boost;
    }

}
