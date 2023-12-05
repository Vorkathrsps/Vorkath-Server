package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.utility.Utils;
import org.jetbrains.annotations.NotNull;

public class AbyssalTentacle extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(@NotNull Entity entity, Entity target) {
        entity.animate(1658);
        entity.submitHit(target, 0, this)
            .postDamage(h -> {
                if (!h.isAccurate()) {
                    h.block();
                    return;
                }
                target.graphic(341, GraphicHeight.HIGH, 0);
                target.freeze(8, entity, true);
                if (Utils.rollDice(25)) target.poison(4);
            });
        CombatSpecial.drain(entity, CombatSpecial.ABYSSAL_TENTACLE.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(@NotNull Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
