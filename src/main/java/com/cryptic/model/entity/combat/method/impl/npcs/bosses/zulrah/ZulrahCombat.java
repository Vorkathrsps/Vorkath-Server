package com.cryptic.model.entity.combat.method.impl.npcs.bosses.zulrah;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.chainedwork.Chain;

public class ZulrahCombat extends CommonCombatMethod {
    boolean initiated = false;
    @Override
    public void init(NPC npc) {
        npc.lockDamageOk();
        npc.animate(5073);
        Chain.noCtx().runFn(2, npc::unlock);
    }
    @Override
    public boolean prepareAttack(Entity entity, Entity target) {
        if (!initiated) return false;
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
