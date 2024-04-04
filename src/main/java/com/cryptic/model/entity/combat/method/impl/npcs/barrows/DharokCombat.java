package com.cryptic.model.entity.combat.method.impl.npcs.barrows;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.CombatType;
import com.cryptic.model.entity.combat.hit.Hit;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;

public class DharokCombat extends CommonCombatMethod {
    @Override
    public void init(NPC npc) {
        npc.ignoreOccupiedTiles = true;
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) return false;
        entity.animate(entity.attackAnimation());
        int maxHp = entity.getAsNpc().maxHp();
        int currentHp = entity.getAsNpc().hp();
        double multiplier = (double) 1 + (double) (maxHp - currentHp) / 100 * ((double) maxHp / 100);
        new Hit(entity, target, CombatFactory.calcDamageFromType(entity, target, CombatType.MELEE), 0, CombatType.MELEE).checkAccuracy(true).submit().postDamage(hit -> {
            if (hit.isAccurate()) hit.damageModifier(multiplier);
        });
        return true;
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
