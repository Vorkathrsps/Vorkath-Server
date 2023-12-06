package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Skills;

public class AbyssalBludgeon extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(3299);
        var hit = entity.submitHit(target, 0, this).postDamage(h -> {
            if (!h.isAccurate()) {
                h.block();
                return;
            }
            h.setDamage((int) (h.getDamage() * (1 + (((entity.getSkills().xpLevel(Skills.PRAYER) - entity.getSkills().level(Skills.PRAYER)) * 0.5)) / 100)));
            target.graphic(1284, GraphicHeight.LOW, 0);
        });
        entity.sendSound(2715, hit.getDelay());
        entity.sendSound(1930, hit.getDelay());
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
