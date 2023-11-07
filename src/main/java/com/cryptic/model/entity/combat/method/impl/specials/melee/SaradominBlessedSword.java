package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

public class SaradominBlessedSword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1133);
        entity.graphic(1213, GraphicHeight.HIGH, 0);
        new Hit(entity, target, 1, this).checkAccuracy(true).submit();
        target.graphic(1196, GraphicHeight.MIDDLE, 0);
        CombatSpecial.drain(entity, CombatSpecial.BLESSED_SARADOMIN_SWORD.getDrainAmount());
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
