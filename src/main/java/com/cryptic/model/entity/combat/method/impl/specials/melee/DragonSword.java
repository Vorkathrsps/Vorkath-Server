package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class DragonSword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(7515);
        entity.graphic(1369, GraphicHeight.HIGH, 0);
        var hit = entity.submitHit(target, 1, this);
        entity.sendSound(3552, hit.getDelay());
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_SWORD.getDrainAmount());
        //TODO If the target is using Protect from Melee, the special attack will ignore the prayer for one attack.
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
