package com.cryptic.model.entity.combat.method.impl.npcs.bosses.demonicgorillas;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

/**
 * @author Patrick van Elderen | March, 13, 2021, 22:10
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class DemonicGorillaMeleeStrategy extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        //mob.forceChat("MELEE!");
        entity.animate(entity.attackAnimation());
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 1, CombatType.MELEE).checkAccuracy(true).postDamage(h -> ((DemonicGorilla)entity).getCombatAI().handleAfterHit(h)).submit();
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
