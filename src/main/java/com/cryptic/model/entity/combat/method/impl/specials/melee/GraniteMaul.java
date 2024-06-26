package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.World;
import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

/**
 * Granite maul
 *
 * @author Gabriel Hannason
 */
public class GraniteMaul extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        int specPercentage = entity.getSpecialAttackPercentage();

        if (specPercentage < entity.getAsPlayer().getCombatSpecial().getDrainAmount()) {
            entity.message("You do not have enough special attack energy left!");
            entity.setSpecialActivated(false);
            CombatSpecial.updateBar(entity.getAsPlayer());
            return false;
        }
        
        entity.animate(1667);
        entity.graphic(340, GraphicHeight.HIGH, 0);

        int delay = 0;
        if (entity.isPlayer() && target.isPlayer()) {
            int renderIndexOf = World.getWorld().getPlayers().getRenderOrderInternal().indexOf(entity.getIndex());
            int renderIndexOf2 = World.getWorld().getPlayers().getRenderOrderInternal().indexOf(target.getIndex());
            delay = renderIndexOf > renderIndexOf2 ? 1 : 0;
        }

        var hit = entity.submitHit(target, delay, this);
        entity.sendPublicSound(2715, hit.getDelay());
        CombatSpecial.drain(entity, entity.getAsPlayer().getCombatSpecial().getDrainAmount());
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
