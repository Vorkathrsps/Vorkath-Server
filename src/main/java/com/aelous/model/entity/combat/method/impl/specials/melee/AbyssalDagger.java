package com.aelous.model.entity.combat.method.impl.specials.melee;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public class AbyssalDagger extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(3300);
        entity.graphic(1283, GraphicHeight.LOW, 0);
        //TODO mob.sound(2537);
        //TODO mob.sound(2537); // yes same sound twice on 07

        int h1 = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
        int h2 = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);

        if(h1 > 0) {
            Hit hit = target.hit(entity, h1,1, CombatType.MELEE).checkAccuracy();
            hit.submit();
            Hit hit2 = target.hit(entity, h2,target.isNpc() ? 1 : 1, CombatType.MELEE).checkAccuracy();
            hit2.submit();
        } else {
            //Blocked
            Hit hit = target.hit(entity, 0,1, CombatType.MELEE).setAccurate(false);
            hit.submit();
            Hit hit2 = target.hit(entity, 0,target.isNpc() ? 1 : 1, CombatType.MELEE).setAccurate(false);
            hit2.submit();
        }
        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_DAGGER.getDrainAmount());
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
