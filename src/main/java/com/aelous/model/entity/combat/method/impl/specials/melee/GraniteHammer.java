package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;

/**
 * @author Patrick van Elderen | November, 28, 2020, 19:19
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class GraniteHammer extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1378);
        entity.graphic(1450);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE) + 5,1, CombatType.MELEE).checkAccuracy();
        hit.submit();
        CombatSpecial.drain(entity, CombatSpecial.GRANITE_HAMMER.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}
