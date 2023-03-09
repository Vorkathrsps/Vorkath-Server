package com.aelous.model.entity.combat.method.impl.npcs.barrows;

import com.aelous.model.entity.Entity;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatType;
import com.aelous.model.entity.combat.hit.Hit;
import com.aelous.model.entity.combat.method.impl.CommonCombatMethod;
import com.aelous.model.entity.masks.Projectile;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Skills;
import com.aelous.utility.Utils;

public class Karils extends CommonCombatMethod {

    @Override
    public void prepareAttack(Entity entity, Entity target) {

        if (!withinDistance(8)) {
            return;
        }

        entity.animate(entity.attackAnimation());

        var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());

        new Projectile(entity, target, 27, 41, 12 * tileDist, 30, 30, 0, 10, 15).sendProjectile();

        var delay = Math.max(1, 20 + tileDist * 12 / 30);
        if (delay > 2)
            delay = 2;

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy();

        if (Utils.rollPercent(25)) {
            if (hit.isAccurate()) {
                taintedShot();
                hit.submit();
            }
        } else {
            hit.submit();
        }
    }

    private void taintedShot() {
        if (target != null) {
            target.graphic(401, GraphicHeight.HIGH, 0);
            target.getSkills().setLevel(Skills.AGILITY, (target.getSkills().level(Skills.AGILITY) - reductionFormula()));
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
