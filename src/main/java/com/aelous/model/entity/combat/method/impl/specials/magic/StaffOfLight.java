package com.aelous.model.entity.combat.method.impl.specials.magic;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.utility.timers.TimerKey;

public class StaffOfLight extends CommonCombatMethod  {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        entity.animate(7967);
        entity.graphic(1516, GraphicHeight.HIGH_2, 0);
        entity.getTimers().addOrSet(TimerKey.SOTD_DAMAGE_REDUCTION, 60);
        entity.message("<col=3d5d2b>Spirits of deceased evildoers offer you their protection.");
        CombatSpecial.drain(entity, CombatSpecial.TOXIC_STAFF_OF_THE_DEAD.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 0;
    }
}
