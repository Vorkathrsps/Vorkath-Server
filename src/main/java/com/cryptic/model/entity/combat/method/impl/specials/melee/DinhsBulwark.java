package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

public class DinhsBulwark extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(7511);
        entity.graphic(1336);
        new Hit(entity, target, 1, this).checkAccuracy(true).submit();
        //TODO effect Dinh's bulwark has a special attack, Shield Bash, which hits up to 10 enemies in a 11x11 area around the player (thus up to five tiles away from the player)
        // The targeted monster gets hit twice by the attack, while all other monsters get hit once.
        CombatSpecial.drain(entity, CombatSpecial.DINHS_BULWARK.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
