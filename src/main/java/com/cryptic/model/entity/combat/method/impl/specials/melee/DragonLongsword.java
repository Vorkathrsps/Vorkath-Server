package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class DragonLongsword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1058);
        entity.graphic(248, GraphicHeight.HIGH, 0);
        entity.submitHit(target, 1, this);
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_LONGSWORD.getDrainAmount());
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
