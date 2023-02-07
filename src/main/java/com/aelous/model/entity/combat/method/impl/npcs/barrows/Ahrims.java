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
    public void prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return;
        }

        entity.animate(entity.attackAnimation());

        var tileDist = entity.tile().transform(3, 3, 0).distance(target.tile());

        new Projectile(entity, target, 156, 40, 12 * tileDist, 30, 30, 0).sendProjectile();

        var delay = Math.max(Math.max(1, 20 + tileDist * 12 / 30), 4);

        if (delay > 2)
            delay = 2;

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy();

        if (hit.isAccurate()) {
            if (Utils.rollPercent(20)) {
                blightedAura();
                hit.submit();
            }
        } else {
            hit.submit();
        }
    }

    private void blightedAura() {
        if (target != null) {
            target.graphic(400, GraphicHeight.HIGH, 0);
            target.skills().setLevel(Skills.STRENGTH, (target.skills().level(Skills.STRENGTH) - 5));
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
