package com.aelous.model.content.raids.chamber_of_xeric.great_olm;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;

/**
 * @author Patrick van Elderen | May, 16, 2021, 13:11
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class GreatOlmCombatMethod extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {

    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 5;
    }
}
