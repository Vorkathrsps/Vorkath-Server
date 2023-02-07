package com.aelous.model.entity.combat.method.impl.specials.melee;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.utility.Utils;

public class DragonClaws extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        entity.animate(7514);
        entity.graphic(1171);

        int first = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
        int secondHit = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);

        /* DRAGON CLAWS SPECIAL FORMULA */
        int maxHit = entity.getCombat().maximumMeleeHit();
        int second = first <= 0 ? secondHit : (first / 2);
        int third;
        int fourth;
        if (second > 0) {
            if (second == 1 || second % 2 == 0) {
                third = second / 2;
                fourth = second / 2;
            } else {
                fourth = 1 + (second / 2);
                third = fourth - 1;
            }
        } else {
            if (first <= 0) {
                int damage = (int) (maxHit * 0.75);
                third = Utils.random(1, damage);
                fourth = Utils.random(1, damage);
            } else {
                third = 0;
                fourth = 0;
            }
        }
        // If all 3 hits fail then we empower the fourth.
        if (first <= 0 && second <= 0 && third <= 0)
            fourth = Utils.random(maxHit, (int) (maxHit * 1.5));

        Hit hit = target.hit(entity, first,1, CombatType.MELEE).checkAccuracy();
        hit.submit();
        Hit hit2 = target.hit(entity, second,1, CombatType.MELEE).checkAccuracy();
        hit2.submit();
        Hit hit3 = target.hit(entity, third,2, CombatType.MELEE).checkAccuracy();
        hit3.submit();
        Hit hit4 = target.hit(entity, fourth,2, CombatType.MELEE).checkAccuracy();
        hit4.submit();
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_CLAWS.getDrainAmount());
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
