package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.combat.damagehandler.registery.ListenerRegistry;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.ItemIdentifiers;
import com.aelous.utility.Utils;

public class AmuletOfBloodFury implements DamageEffectListener {
    public AmuletOfBloodFury() {
        ListenerRegistry.registerListener(this);
    }

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        int damage = hit.getDamage();

        if (combatType != null) {
            if (!(entity instanceof Player player) || !combatType.isMelee() || !hit.isAccurate()) {
                return false;
            }

            if (!player.getEquipment().contains(ItemIdentifiers.AMULET_OF_BLOOD_FURY)) {
                return false;
            }

            if (Utils.percentageChance(20)) {
                if (damage > 0) {
                    int healAmount = damage * 30 / 100;
                    player.heal(healAmount);
                    player.graphic(1542);
                    return true;
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

