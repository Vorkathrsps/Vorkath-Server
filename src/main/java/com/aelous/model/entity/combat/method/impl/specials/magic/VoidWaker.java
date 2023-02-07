package com.aelous.model.entity.combat.method.impl.specials.magic;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;

import java.security.SecureRandom;

public class VoidWaker extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        SecureRandom randomGarunteedAccuracy = new SecureRandom();

        double logic = ((entity.getCombat().maximumMeleeHit() * 2.50) / Math.min(Math.max(randomGarunteedAccuracy.nextDouble(), 2.50), 1.50));

        entity.animate(1378);

        Hit hit = target.hit(entity, (int) Math.floor(logic),0, CombatType.MAGIC);
        hit.setAccurate(true);
        hit.submit();

        CombatSpecial.drain(entity, CombatSpecial.VOIDWAKER.getDrainAmount());
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
