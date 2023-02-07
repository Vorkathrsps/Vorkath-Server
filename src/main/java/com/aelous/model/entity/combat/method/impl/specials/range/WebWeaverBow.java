package com.aelous.model.entity.combat.method.impl.specials.range;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.combat.ranged.RangedData;
import com.aelous.utility.chainedwork.Chain;

import java.security.SecureRandom;

public class WebWeaverBow extends CommonCombatMethod {
    @Override
    public void prepareAttack(Entity entity, Entity target) {
        SecureRandom secureRandom = new SecureRandom();

        RangedData.RangedWeapon rangeWeapon = entity.getCombat().getRangedWeapon();
        boolean ignoreArrows = rangeWeapon != null && rangeWeapon.ignoreArrowsSlot();

        double secure = Math.min(Math.max(secureRandom.nextDouble(), 0.40), 0.10);

        double hitLogic = (entity.getCombat().maximumRangedHit(ignoreArrows) * secure);

        boolean chanceToPoison = secureRandom.nextDouble() < .35;

        entity.animate(9964);
        //TODO gfx

        if (chanceToPoison) {
            target.poison(4);
        }

        Hit hit = target.hit(entity, (int) hitLogic,1, CombatType.RANGED).checkAccuracy();

        hit.submit();

        Chain.bound(null).runFn(2, () -> {
                for (int i = 0; i < 3; i++) {
                    Chain.bound(null).runFn(0, hit::submit);
                }
            });

        CombatSpecial.drain(entity, CombatSpecial.WEBWEAVER_BOW.getDrainAmount());

    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }
}
