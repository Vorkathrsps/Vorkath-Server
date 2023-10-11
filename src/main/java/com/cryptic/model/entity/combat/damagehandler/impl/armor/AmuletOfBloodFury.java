package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

import static com.cryptic.utility.ItemIdentifiers.AMULET_OF_BLOOD_FURY;

public class AmuletOfBloodFury implements DamageEffectListener {

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        int damage = hit.getDamage();
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target != null) {
                if (combatType == CombatType.MELEE) {
                    if (player.getEquipment().contains(AMULET_OF_BLOOD_FURY)) {
                        if (hit.isAccurate()) {
                            if (Utils.rollDice(20)) {
                                if (damage > 0) {
                                    int healAmount = damage * 30 / 100;
                                    player.heal(healAmount);
                                    player.graphic(1542);
                                    return true;
                                }
                            }
                        }
                    }
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

