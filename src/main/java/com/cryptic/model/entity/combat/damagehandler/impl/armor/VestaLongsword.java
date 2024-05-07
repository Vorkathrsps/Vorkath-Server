package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.VESTAS_BLIGHTED_LONGSWORD;

public class VestaLongsword implements DamageModifyingListener {

    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            var equipment = player.getEquipment();
            if (combatType == CombatType.MELEE) {
                if (equipment.contains(VESTAS_BLIGHTED_LONGSWORD)) {
                    var modifier = accuracy.modifier();
                    if (player.isSpecialActivated()) {
                        modifier += 1.25F;
                        return (int) modifier;
                    }
                }
            }
        }
        return 0;
    }

}
