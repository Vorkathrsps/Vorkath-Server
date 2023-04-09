package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.utility.Utils;

public class VestaLongsword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(7515);

        boolean isDummy = target != target.getAsPlayer() && target.getAsNpc().isCombatDummy();

        int maxHit = entity.getCombat().getMaximumMeleeDamage();
        int minhit = (int) (maxHit * 0.2);

        int damage = Utils.random(minhit, maxHit);

        Hit hit = target.hit(entity, isDummy ? maxHit : damage,1, CombatType.MELEE).checkAccuracy();

        hit.submit();

        CombatSpecial.drain(entity, CombatSpecial.VESTAS_BLIGHTED_LONGSWORD.getDrainAmount());
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
