package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

/**
 * @author Origin | January, 30, 2021, 13:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class BoneDagger extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(4198);
        entity.graphic(704);
        entity.submitHit(target, 1, this);
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
