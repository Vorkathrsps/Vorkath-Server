package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.cryptic.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.cryptic.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.utility.ItemIdentifiers.AMULET_OF_BLOOD_FURY;

public class AmuletOfBloodFury implements DamageEffectListener {

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        int damage = hit.getDamage();
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (checkCombatType(combatType, target)) return false;
            if (player.getEquipment().contains(AMULET_OF_BLOOD_FURY)) {
                if (checkConditions(hit, damage)) return false;
                int healAmount = damage * 30 / 100;
                player.heal(healAmount);
                player.graphic(1542);
                return true;
            }
        }
        return false;
    }

    private static boolean checkCombatType(CombatType combatType, Entity target) {
        if (target == null) return true;
        if (combatType != CombatType.MELEE) return true;
        return false;
    }

    private static boolean checkConditions(Hit hit, int damage) {
        if (hit.isAccurate() && damage == 0) return true;
        if (!hit.isAccurate()) return true;
        if (!World.getWorld().rollDie(20, 1)) return true;
        return damage == 0;
    }
}

