package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class DragonHalberd extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1203);
        entity.graphic(1231, GraphicHeight.HIGH, 0);
        if (target.getSize() == 1) {
            var hit = entity.submitHit(target, 1, this);
            entity.sendPublicSound(2533, hit.getDelay());
        } else {
            var hit = entity.submitHit(target, 1, this);
            entity.submitHit(target, 1, this);
            entity.sendPublicSound(2533, hit.getDelay());
        }
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_HALBERD.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 3;
    }
}
