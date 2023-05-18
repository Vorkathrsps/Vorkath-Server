package com.aelous.model.entity.combat.method.effects.equipment.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatConstants;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.ListenerRegistry;
import com.aelous.model.entity.combat.method.effects.AbilityListener;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.timers.TimerKey;

import static com.aelous.utility.ItemIdentifiers.*;

public class ToxicStaffOfTheDead implements AbilityListener {
    public ToxicStaffOfTheDead() {
        ListenerRegistry.registerListener(this);
    }
    @Override
    public boolean prepareEffect(Entity entity, CombatType combatType, Hit hit) {
        var player = (Player) entity;
        if (player.getTimers().has(TimerKey.SOTD_DAMAGE_REDUCTION)
            && player.getEquipment().containsAny(STAFF_OF_THE_DEAD, TOXIC_STAFF_OF_THE_DEAD, TOXIC_STAFF_UNCHARGED, STAFF_OF_LIGHT)
            && combatType == CombatType.MELEE) {
            int damage = hit.getDamage();
            damage = (int) Math.floor(damage * CombatConstants.TSTOD_DAMAGE_REDUCTION);
            hit.setDamage(damage);
            return true;
        }
        return false;
    }
}
