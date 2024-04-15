package com.cryptic.model.entity.combat.damagehandler.impl.armor;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatConstants;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.damagehandler.listener.DamageModifyingListener;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.timers.TimerKey;

import static com.cryptic.utility.ItemIdentifiers.*;

public class ToxicStaffOfTheDead implements DamageModifyingListener {

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        if (entity instanceof Player player) {
            var target = player.getCombat().getTarget();
            if (target instanceof Player enemy) {
                if (player.getEquipment().containsAny(STAFF_OF_THE_DEAD, TOXIC_STAFF_OF_THE_DEAD, TOXIC_STAFF_UNCHARGED, STAFF_OF_LIGHT)) {
                    if (player.getTimers().has(TimerKey.SOTD_DAMAGE_REDUCTION)) {
                        if (enemy.getCombat().getCombatType() == CombatType.MELEE) {
                            if (hit.isAccurate()) {
                                if (hit.getDamage() > 0) {
                                    int damage = hit.getDamage();
                                    var reduced_value = damage - (damage * CombatConstants.TSTOD_DAMAGE_REDUCTION);
                                    damage = (int) reduced_value;
                                    hit.setDamage(damage);
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
}
