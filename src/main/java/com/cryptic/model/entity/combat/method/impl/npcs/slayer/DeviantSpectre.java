package com.cryptic.model.entity.combat.method.impl.npcs.slayer;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.Graphic;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class DeviantSpectre extends CommonCombatMethod {

    //TODO: find correct anim and gfx

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.performGraphic(new Graphic(96, GraphicHeight.HIGH));
        entity.animate(entity.attackAnimation());
        entity.performGraphic(new Graphic(98, GraphicHeight.HIGH));
        int tileDist = entity.tile().transform(1, 1).distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 97, 51, duration, 43, 31, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        target.hit(entity, CombatFactory.calcDamageFromType(entity, target,CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true).submit();
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 6;
    }
}
