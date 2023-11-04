package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

public class OsmumtensFang extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1378);
        entity.graphic(2128);


            /**
             *         if (attackBonus > defenceBonus)
             *             successfulRoll = 1 - (defenceBonus + 2) * (2D + 3);
             *         else
             *             successfulRoll = ((attackBonus * ((4 * attackBonus) + 5)) / (6 * (attackBonus + 1) * (defenceBonus + 1))));
             *
             */

            Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),0, CombatType.MELEE).checkAccuracy(true);

            hit.submit();
        return true;

    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }
}
