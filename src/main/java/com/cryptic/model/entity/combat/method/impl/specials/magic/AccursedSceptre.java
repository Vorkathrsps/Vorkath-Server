package com.cryptic.model.entity.combat.method.impl.specials.magic;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.player.Skills;

public class AccursedSceptre extends CommonCombatMethod {
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        int tileDist = entity.tile().getChevDistance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        entity.animate(9961);
        entity.graphic(2338, GraphicHeight.HIGH, 0);
        Projectile p = new Projectile(entity, target, 2339, 51, duration, 30, 0, 0, entity.getSize(), 10);
        final int delay = entity.executeProjectile(p);
        Hit hit = Hit.builder(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);
        hit.submit();
        hit.postDamage(t -> {
            var mlvl = target.getSkills().level(Skills.MAGIC);
            var dlvl = target.getSkills().level(Skills.DEFENCE);
            double drainPercentage = 0.15; // 15% drain
            int minLevel = (int) Math.ceil(drainPercentage * mlvl);

            if (dlvl - minLevel < 0) {
                minLevel = dlvl;
            }

            target.getSkills().setLevel(Skills.DEFENCE, minLevel);
            target.getSkills().setLevel(Skills.MAGIC, minLevel);
        });
        target.graphic(157, GraphicHeight.MIDDLE, p.getSpeed());
        return false;
    }


    @Override
    public int getAttackSpeed(Entity entity) {
        return 0;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 0;
    }
}
