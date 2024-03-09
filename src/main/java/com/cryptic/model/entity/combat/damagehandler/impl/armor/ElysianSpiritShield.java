package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

import static com.cryptic.utility.ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD;

public class ElysianSpiritShield implements DamageEffectListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var target = entity.getCombat().getTarget();
        var setIgnoreElysianReduction = hit.reflected ? 1 : CombatConstants.ELYSIAN_DAMAGE_REDUCTION;
        if (target instanceof Player player) {
            if (player.getEquipment().hasAt(EquipSlot.SHIELD, ELYSIAN_SPIRIT_SHIELD)) {
                if (hit.isAccurate()) {
                    if (Utils.rollDice(70)) {
                        int damage = hit.getDamage();
                        var reduced_value = damage - (damage * setIgnoreElysianReduction);
                        hit.setDamage((int) reduced_value);
                        player.performGraphic(new Graphic(321, GraphicHeight.LOW, 0));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

