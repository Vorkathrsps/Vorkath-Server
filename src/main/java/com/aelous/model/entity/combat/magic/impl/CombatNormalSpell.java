package com.aelous.model.entity.combat.magic.impl;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.magic.CombatSpell;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;

public abstract class CombatNormalSpell extends CombatSpell {

    /**
     * @param cast
     * @param castOn
     * @param accurate
     * @param damage
     */
    @Override
    public final void finishCast(Entity cast, Entity castOn, boolean accurate, int damage) {
    }
}
