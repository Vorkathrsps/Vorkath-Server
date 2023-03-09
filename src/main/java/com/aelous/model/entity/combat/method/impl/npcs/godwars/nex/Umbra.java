package com.aelous.model.entity.combat.method.impl.npcs.godwars.nex;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.player.Skills;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since January 13, 2022
 */
public class Umbra extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {
        entity.animate(entity.attackAnimation());
        if (target.isPlayer()) {
            Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), 2, CombatType.MAGIC);
            hit.checkAccuracy().submit();
            if (hit.isAccurate()) {
                target.graphic(383);

                if (target.getSkills().level(Skills.ATTACK) < target.getSkills().xpLevel(Skills.ATTACK)) {
                    return;
                }

                int decrease = (int) (0.15 * (target.getSkills().level(Skills.ATTACK)));
                target.getSkills().setLevel(Skills.ATTACK, target.getSkills().level(Skills.ATTACK) - decrease);
                target.getSkills().update(Skills.ATTACK);
            }
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
