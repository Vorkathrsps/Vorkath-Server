package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.EquipSlot;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

import static com.cryptic.utility.ItemIdentifiers.ELYSIAN_SPIRIT_SHIELD;

public class ElysianSpiritShield implements DamageModifyingListener {
    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        var target = entity.getCombat().getTarget();
        var setIgnoreElysianReduction = hit.reflected ? 1 : CombatConstants.ELYSIAN_DAMAGE_REDUCTION;
        if (target instanceof Player player) {
            if (!player.getEquipment().hasAt(EquipSlot.SHIELD, ELYSIAN_SPIRIT_SHIELD)) return false;
            if (hit.isAccurate()) {
                final int randomRoll = World.getWorld().random().nextInt(100);
                if (randomRoll < 70) {
                    int damage = hit.getDamage();
                    var reduced_value = damage - (damage * setIgnoreElysianReduction);
                    hit.setDamage((int) reduced_value);
                    player.performGraphic(new Graphic(321, GraphicHeight.LOW, 0));
                    return true;
                }
            }
        }
        return false;
    }
}

