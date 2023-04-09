package com.aelous.model.entity.combat.method.impl.npcs.slayer;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.Graphic;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

public class DeviantSpectre extends CommonCombatMethod {

    //TODO: find correct anim and gfx

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.performGraphic(new Graphic(96, GraphicHeight.HIGH));
        entity.animate(entity.attackAnimation());
        entity.performGraphic(new Graphic(98, GraphicHeight.HIGH));
        new Projectile(entity, target, 97, 40, 60, 43, 31, 0).sendProjectile();
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy().submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 6;
    }
}
