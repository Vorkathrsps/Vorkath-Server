package com.aelous.model.entity.combat.method.impl.specials.melee;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public class DragonDagger extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1062);
        entity.graphic(252, GraphicHeight.HIGH, 0);
        //TODO it.player().world().spawnSound(it.player().tile(), 2537, 0, 10)

        int h1 = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
        int h2 = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
        Hit hit = target.hit(entity, h1,1, CombatType.MELEE).checkAccuracy();
        hit.submit();
        Hit hit2 = target.hit(entity, h2,target.isNpc() ? 0 : 1, CombatType.MELEE).checkAccuracy();
        hit2.submit();
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_DAGGER.getDrainAmount());
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
