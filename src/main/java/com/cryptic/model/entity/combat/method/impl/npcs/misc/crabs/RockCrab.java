package com.cryptic.model.entity.combat.method.impl.npcs.misc.crabs;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import org.apache.commons.lang.ArrayUtils;

public class RockCrab extends CommonCombatMethod {
    int[] ids = new int[]{100, 102, 5935, 7206};
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!withinDistance(1)) return false;
        entity.animate(1312);
        entity.submitHit(target, 0, this);
        return true;
    }

    @Override
    public int getAttackSpeed(Entity entity) {
        return 4;
    }

    @Override
    public int moveCloseToTargetTileRange(Entity entity) {
        return 1;
    }

    @Override
    public void onRespawn(NPC npc) {
        if (ArrayUtils.contains(ids, npc.id())) {
            npc.transmog(npc.id() + 1, true);
        }
    }
}
