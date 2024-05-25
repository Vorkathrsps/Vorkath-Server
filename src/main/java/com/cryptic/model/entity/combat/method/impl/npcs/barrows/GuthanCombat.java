package com.cryptic.model.entity.combat.method.impl.npcs.barrows;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.Utils;

public class GuthanCombat extends CommonCombatMethod {
    @Override
    public void init(NPC npc) {
        npc.ignoreOccupiedTiles = true;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) {
            return false;
        }

        entity.animate(entity.attackAnimation());

        Hit hit = target.hit(entity, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true);

        if (Utils.rollPercent(25)) {
            infestation(hit);
        }

        hit.submit();
        return true;
    }

    private Entity infestation(Hit hit) {
        int heal = 0;
        int getNpcHp = entity.getAsNpc().hp();
        int getDamage = hit.getDamage();
        if (hit.isAccurate()) {
            heal += getDamage;
        }
        return entity.setHitpoints(getNpcHp + heal);
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return entity.getAsNpc().getBaseAttackSpeed();
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }
}
