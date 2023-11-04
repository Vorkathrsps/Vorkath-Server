package com.cryptic.model.entity.combat.method.impl.npcs.barrows;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;

public class Ahrims extends CommonCombatMethod {

    @Override
    public void init(NPC npc) {
        npc.ignoreOccupiedTiles = true;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(8)) {
            return false;
        }

        entity.animate(entity.attackAnimation());

        var tileDist = entity.tile().distance(target.tile());
        int duration = (51 + -5 + (10 * tileDist));
        Projectile p = new Projectile(entity, target, 156, 51, duration, 43, 31, 6, 1, 10);
        final int delay = entity.executeProjectile(p);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MAGIC), delay, CombatType.MAGIC).checkAccuracy(true);

        hit.submit();

        if (hit.isAccurate()) {
            if (Utils.rollPercent(20)) {
                blightedAura();
            }
        } else {
            target.graphic(85, GraphicHeight.MIDDLE, p.getSpeed());
        }

        return true;
    }

    private void blightedAura() {
        var reduction = 5;
        if (target != null) {
            target.graphic(400, GraphicHeight.HIGH, 0);
            if (target.getSkills().level(Skills.STRENGTH) < reduction) {
                target.getSkills().alterSkill(Skills.STRENGTH, 0);
            } else {
                target.getSkills().alterSkill(Skills.STRENGTH, (target.getSkills().level(Skills.STRENGTH) - 5));
            }
        }
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}
