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

public class Ahrims extends CommonCombatMethod {

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }

        entity.animate(entity.attackAnimation());

        //var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());

        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 156, 51, duration, 43, 31, 0, target.getSize(), 10);
        final int delay = entity.executeProjectile(p);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();

        if (hit.isAccurate()) {
            if (Utils.rollPercent(20)) {
                blightedAura();
                hit.submit();
            }
        } else {
            hit.submit();
        }
        return true;
    }

    private void blightedAura() {
        if (target != null) {
            target.graphic(400, GraphicHeight.HIGH, 0);
            target.getSkills().setLevel(Skills.STRENGTH, (target.getSkills().level(Skills.STRENGTH) - 5));
        }
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
