package com.aelous.model.entity.combat.method.impl.npcs.barrows;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Skill;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.Utils;

public class Karils extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {

        if (!withinDistance(8)) {
            return false;
        }

        entity.animate(entity.attackAnimation());

        var tileDist = entity.tile().distance(target.tile());
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 27, 41, duration, 43, 31, 0, target.getSize(), 5);
        final int delay = entity.executeProjectile(p);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();

        if (Utils.rollPercent(25)) {
            if (hit.isAccurate()) {
                taintedShot();
                hit.submit();
            }
        } else {
            hit.submit();
        }
        return true;
    }

    private void taintedShot() {
        if (target != null) {
            target.graphic(401, GraphicHeight.HIGH, 0);
            if (target.getSkills().getMaxLevel(Skill.AGILITY) > 1) {
                target.getSkills().setLevel(Skills.AGILITY, (target.getSkills().level(Skills.AGILITY) - reductionFormula()));
            }
        }
    }

    private int reductionFormula() {
        int skill = target.getSkills().level(Skills.AGILITY);
        int reduction = skill - (skill * 20) / 100;
        return (skill - reduction);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int getAttackDistance(Entity entity) {
        return 8;
    }
}
