package com.cryptic.model.entity.combat.method.impl.specials.melee;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;

/**
 * Vesta's spear has a special attack, Spear Wall, that consumes 50% of the player's special attack energy and damages up to 16 targets within 8 tiles surrounding the player (one if the player is outside a multicombat area).
 * <p>
 * In addition, the user becomes immune to melee attacks for 8 ticks (4.8 seconds).
 */
public class VestaSpear extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(8184);
        entity.graphic(1627);
        entity.submitHit(target, 1, this);
        CombatSpecial.drain(entity, CombatSpecial.VESTA_SPEAR.getDrainAmount());
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
