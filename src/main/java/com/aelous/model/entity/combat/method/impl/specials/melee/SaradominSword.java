package com.aelous.model.entity.combat.method.impl.specials.melee;

import com.aelous.model.World;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.formula.accuracy.AccuracyFormula;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;

/**
 * The Saradomin sword has a special attack, Saradomin's Lightning, that deals 10% more melee damage and 1-16 extra Magic damage.
 * This special attack consumes 100% of the wielder's special attack energy. The special attack rolls against the opponent's Magic defence bonus using the player's
 * slash attack bonus, thus making this special attack extremely accurate on melee armour. If the melee attack misses, then the Magic attack will also fail. However,
 * if the melee hit is a successful hit but rolls a 0, the Magic damage will still be applied. Players receive 2 Magic experience for each point of damage caused by the
 * extra Magic damage. The Magic damage will always hit 0 on cyclopes in the Warriors' Guild, as with all non-melee damage there. It will also hit 0 on Callisto,
 * Venenatis and Vet'ion as they are immune to Magic damage.
 */
public class SaradominSword extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(1132);
        entity.graphic(1213, GraphicHeight.HIGH, 0);

        boolean accurate = AccuracyFormula.doesHit(entity, target, CombatType.MELEE);
        int meleeHit = CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE);
        int magicHit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);
        if (accurate) {
            if (meleeHit > 0) {
                magicHit = World.getWorld().random(1, 16);
            }
        } else {
            meleeHit = 0;
            magicHit = 0;
        }

        Hit hit = target.hit(entity, meleeHit,1, CombatType.MELEE).checkAccuracy();
        hit.submit();
        Hit hit2 = target.hit(entity, magicHit,1, CombatType.MAGIC).checkAccuracy();
        hit2.submit();
        CombatSpecial.drain(entity, CombatSpecial.SARADOMIN_SWORD.getDrainAmount());
return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 1;
    }
}
