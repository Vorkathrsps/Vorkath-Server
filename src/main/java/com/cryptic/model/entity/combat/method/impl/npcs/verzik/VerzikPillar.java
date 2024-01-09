package com.cryptic.model.entity.combat.method.impl.npcs.verzik;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;

public class VerzikPillar extends CommonCombatMethod {
    @Override
    public void init(NPC npc) {
        npc.noRetaliation(true);
        npc.getCombat().setAutoRetaliate(false);
    }

    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
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
