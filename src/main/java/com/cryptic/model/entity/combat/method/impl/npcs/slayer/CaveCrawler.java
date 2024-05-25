package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.utility.Utils;

/**
 * @author PVE
 * @Since augustus 06, 2020
 */
public class CaveCrawler extends CommonCombatMethod {

    private void basicAttack(Entity entity, Entity target) {
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit();
        entity.animate(entity.attackAnimation());
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        basicAttack(entity, target);
        entity.heal(1);
        if (Utils.rollDie(4, 1))
            target.poison(4);
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
