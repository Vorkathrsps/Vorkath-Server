package com.aelous.model.entity.combat.damagehandler.impl.armor;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.MagicAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.MeleeAccuracy;
import com.aelous.model.entity.combat.formula.accuracy.RangeAccuracy;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.damagehandler.registery.ListenerRegistry;
import com.aelous.model.entity.combat.damagehandler.listener.DamageEffectListener;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.utility.ItemIdentifiers.*;

public class ToxicStaffOfTheDead implements DamageEffectListener {
    public ToxicStaffOfTheDead() {
        ListenerRegistry.registerListener(this);
    }

    @Override
    public boolean prepareDamageEffectForAttacker(Entity entity, CombatType combatType, Hit hit) {
        return false;
    }

    @Override
    public boolean prepareDamageEffectForDefender(Entity entity, CombatType combatType, Hit hit) {
        var defender = (Player) entity;
        if (defender.getTimers().has(TimerKey.SOTD_DAMAGE_REDUCTION)
            && defender.getEquipment().containsAny(STAFF_OF_THE_DEAD, TOXIC_STAFF_OF_THE_DEAD, TOXIC_STAFF_UNCHARGED, STAFF_OF_LIGHT)
            && combatType == CombatType.MELEE) {
            int damage = hit.getDamage();
            damage = (int) Math.floor(damage * CombatConstants.TSTOD_DAMAGE_REDUCTION);
            hit.setDamage(damage);
            return true;
        }
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
