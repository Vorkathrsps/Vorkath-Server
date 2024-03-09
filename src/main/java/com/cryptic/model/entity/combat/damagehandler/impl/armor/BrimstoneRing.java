package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

import static com.cryptic.utility.ItemIdentifiers.BRIMSTONE_RING;

public class BrimstoneRing implements DamageEffectListener {
    @Override
    public int prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        if (entity instanceof Player player) {
            if (player.getEquipment().contains(BRIMSTONE_RING)) {
                if (combatType == CombatType.MAGIC) {
                    if (Utils.rollDice(25)) {
                        player.message(Color.RED.wrap("Your attack ignored 10% of your opponent's magic defence."));
                        var modifier = accuracy.modifier();
                        modifier += 1.10F;
                        return modifier;
                    }
                }
            }
        }
        return 0;
    }
}
