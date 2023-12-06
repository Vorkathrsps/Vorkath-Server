package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Player;

public class DragonScimitar extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1872);
        entity.graphic(347, GraphicHeight.HIGH, 0);
        var hit = entity.submitHit(target, 1, this).postDamage(h -> {
            if (!h.isAccurate()) {
                h.block();
                return;
            }
            if (!(target instanceof Player player)) return;
            CombatFactory.disableProtectionPrayers(player);
            player.message("Your target can no longer use protection prayers.");
        });
        entity.sendSound(2540, hit.getDelay());
        CombatSpecial.drain(entity, CombatSpecial.DRAGON_SCIMITAR.getDrainAmount());
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
