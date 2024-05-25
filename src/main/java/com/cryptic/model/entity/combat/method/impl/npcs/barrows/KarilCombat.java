package com.cryptic.model.entity.combat.method.impl.npcs.barrows;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.masks.Projectile;
import com.cryptic.model.entity.masks.impl.graphics.GraphicHeight;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Skill;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.utility.Utils;

public class KarilCombat extends CommonCombatMethod {
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
        int duration = (41 + 11 + (5 * tileDist));
        Projectile p = new Projectile(entity, target, 27, 41, duration, 43, 31, 6, 1, 5);
        final int delay = entity.executeProjectile(p);

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.RANGED), delay, CombatType.RANGED).checkAccuracy(true);

        hit.submit();

        if (hit.isAccurate()) {
            if (Utils.rollPercent(25)) {
                taintedShot();
            }
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
    public int moveCloseToTargetTileRange(Entity entity) {
        return 8;
    }
}
