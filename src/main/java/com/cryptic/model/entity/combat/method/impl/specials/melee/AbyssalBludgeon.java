package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Skills;

public class AbyssalBludgeon extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(3299);
        new Hit(entity, target, 0, this)
            .checkAccuracy(true)
            .submit()
            .postDamage(hit -> {
                if (!hit.isAccurate()) {
                    hit.block();
                    return;
                }
                hit.setDamage((int) (hit.getDamage() * (1 + (((entity.getSkills().xpLevel(Skills.PRAYER) - entity.getSkills().level(Skills.PRAYER)) * 0.5)) / 100)));
                target.graphic(1284, GraphicHeight.LOW, 0);
            });
        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_BLUDGEON.getDrainAmount());
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
