package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class DragonDagger extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1062);
        entity.graphic(252, GraphicHeight.HIGH, 0);
        var hit = entity.submitHit(target, 0, this);
        entity.submitHit(target, 0, this);
        entity.sendSound(2537, hit.getDelay());
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_DAGGER.getDrainAmount());
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
