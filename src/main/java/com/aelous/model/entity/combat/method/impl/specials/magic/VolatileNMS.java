package com.aelous.model.entity.combat.method.impl.specials.magic;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

/**
 * The volatile nightmare staff has a special attack, Immolate, that consumes 55% of the player's special attack energy
 * to hit the target with 50% increased accuracy and dealing a high amount of damage. The special attack does not consume runes.
 *
 * The base damage for Immolate is scaled based on the player's Magic level, ranging from 50 at level 75 to 66 at level 99,
 * which can be increased with magic damage boosting items. At level 99, the maximum possible hit is 80, or 89 while on a
 * slayer assignment.
 *
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date februari 10, 2020 13:14
 */
public class VolatileNMS extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.graphic(1760);
        entity.animate(8532);

        entity.getCombat().setCastSpell(CombatSpells.VOLATILE_NIGHTMARE_STAFF.getSpell());
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 3, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        if(hit.isAccurate()) {
            target.graphic(1759, GraphicHeight.LOW, 0);
        }
        //Reset spell
        entity.getCombat().setCastSpell(null);

        //Drain spec after the attack
        CombatSpecial.drain(entity, CombatSpecial.VOLATILE_NIGHTMARE_STAFF.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 10;
    }
}
