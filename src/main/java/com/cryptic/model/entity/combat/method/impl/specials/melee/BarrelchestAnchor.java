package com.cryptic.model.entity.combat.method.impl.specials.melee;


import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatSpecial;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;

public class BarrelchestAnchor extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        entity.animate(5870);
        entity.graphic(1027);
        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE),1, CombatType.MELEE).checkAccuracy(true);
        hit.submit();

        if (target.isPlayer()) {
            Player playerTarget = (Player) target;

            int roll = Utils.random(4);
            int skill;
            int deduceVal = (int) (hit.getDamage() * 0.10);

            if (roll == 1) {
                skill = Skills.ATTACK;
            } else if (roll == 2) {
                skill = Skills.DEFENCE;
            } else if (roll == 3) {
                skill = Skills.RANGED;
            } else {
                skill = Skills.MAGIC;
            }

            playerTarget.getSkills().alterSkill(skill, -deduceVal);
        }
        CombatSpecial.drain(entity, CombatSpecial.BARRELSCHEST_ANCHOR.getDrainAmount());
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
