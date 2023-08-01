package com.cryptic.model.entity.combat.magic.impl;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.magic.CombatSpell;

public abstract class CombatNormalSpell extends CombatSpell {

    /**
     * @param cast
     * @param castOn
     * @param accurate
     * @param damage
     */
    @Override
    public final void finishCast(Entity cast, Entity castOn, boolean accurate, int damage) { // final means override not needed
    }
}
