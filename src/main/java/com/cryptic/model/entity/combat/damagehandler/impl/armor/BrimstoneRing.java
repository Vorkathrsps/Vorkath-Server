package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.formula.accuracy.AbstractAccuracy;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Color;
import com.cryptic.utility.Utils;

import static com.cryptic.utility.ItemIdentifiers.BRIMSTONE_RING;

public class BrimstoneRing implements DamageModifyingListener {
    @Override
    public double prepareAccuracyModification(Entity entity, CombatType combatType, AbstractAccuracy accuracy) {
        double boost = 0.0D;
        if (entity instanceof Player player) {
            if (!player.getEquipment().contains(BRIMSTONE_RING) && !CombatType.MAGIC.equals(combatType)) return boost;
            final int randomRoll = World.getWorld().random().nextInt(100);
            if (randomRoll < 25) {
                player.message(Color.RED.wrap("Your attack ignored 10% of your opponent's magic defence."));
                boost = 1.10D;
                return boost;
            }
        }
        return boost;
    }
}
