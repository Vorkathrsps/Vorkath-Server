package com.aelous.model.entity.combat.method.impl.specials.magic;


import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.magic.spells.CombatSpells;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.player.Skills;

/**
 * The eldritch nightmare staff has a special attack, Invocate, that consumes 75% of the player's special attack energy
 * to hit the target for a high amount of damage, and restores the caster's prayer points by 50% of the damage dealt.
 * Invocate can boost the caster's prayer points above their prayer level (like the ancient mace), up to a maximum of 120.
 * The special attack does not consume runes.
 *
 * The base damage for Invocate is scaled based on the player's Magic level, ranging from 39 at level 75 to 50 at level 99,
 * which can be increased with magic damage boosting items. At level 99, the maximum possible hit is 60, or 67 while on a
 * slayer assignment.
 *
 * @author Patrick van Elderen | Zerikoth | PVE
 * @date februari 10, 2020 13:14
 */
public class EldritchNMS extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.graphic(1762);
        entity.animate(8532);

        entity.getCombat().setCastSpell(CombatSpells.ELDRITCH_NIGHTMARE_STAFF.getSpell());
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 2, CombatType.MAGIC).checkAccuracy();
        hit.submit();
        if(hit.isAccurate()) {
            target.graphic(1761);
        }

        if(target.isPlayer()) {
            var drain = hit.getDamage() * 35 / 100; // smite 35% of the damage dealt
            target.getSkills().alterSkill(Skills.PRAYER, -drain);
            entity.heal(drain);
        }

        //Reset spell
        entity.getCombat().setCastSpell(null);

        //Drain spec after the attack
        CombatSpecial.drain(entity, CombatSpecial.ELDRITCH_NIGHTMARE_STAFF.getDrainAmount());
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
