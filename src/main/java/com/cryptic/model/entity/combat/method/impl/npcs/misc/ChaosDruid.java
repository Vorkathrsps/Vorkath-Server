package com.cryptic.model.entity.combat.method.impl.npcs.misc;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;

public class ChaosDruid extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (Utils.rollDie(2, 1)) {
            int hit = CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC);
            target.hit(entity, hit, CombatType.MAGIC).checkAccuracy(true).submit();
            if (hit > 0) {
                //We succeed! Send the player a message
                target.message("You feel weakened.");

                //Remove the players attack level by 1
                target.getSkills().alterSkill(Skills.ATTACK, -1);
                target.graphic(181);
            }
        } else {
            //Else we attack the target with a melee attack, if we're in attacking distance.
            if (entity.tile().distance(target.tile()) <= 1)
                target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), CombatType.MELEE).checkAccuracy(true).submit();
        }

        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 7;
    }
}
