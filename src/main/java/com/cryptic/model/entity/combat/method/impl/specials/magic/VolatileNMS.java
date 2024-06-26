package com.cryptic.model.entity.combat.method.impl.specials.magic;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.magic.spells.CombatSpells;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;

/**
 * The volatile nightmare staff has a special attack, Immolate, that consumes 55% of the player's special attack energy
 * to hit the target with 50% increased accuracy and dealing a high amount of damage. The special attack does not consume runes.
 * <p>
 * The base damage for Immolate is scaled based on the player's Magic level, ranging from 50 at level 75 to 66 at level 99,
 * which can be increased with magic damage boosting items. At level 99, the maximum possible hit is 80, or 89 while on a
 * slayer assignment.
 *
 * @author Origin | Zerikoth | PVE
 * @date februari 10, 2020 13:14
 */
public class VolatileNMS extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.graphic(1760);
        entity.animate(8532);
        entity.getCombat().setCastSpell(CombatSpells.VOLATILE_NIGHTMARE_STAFF.getSpell());
        var hit = entity.submitHit(target, 2, this);
        if (hit.isAccurate()) target.graphic(1759, GraphicHeight.LOW, 0);
        entity.getCombat().setCastSpell(null);
        entity.getCombat().setTarget(null);
        CombatSpecial.drain(entity, CombatSpecial.VOLATILE_NIGHTMARE_STAFF.getDrainAmount());
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 10;
    }
}
