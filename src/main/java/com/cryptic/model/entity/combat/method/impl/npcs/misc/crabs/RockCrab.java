package com.cryptic.model.entity.combat.method.impl.npcs.misc.crabs;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.method.impl.CommonCombatMethod;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.utility.chainedwork.Chain;
import org.apache.commons.lang.ArrayUtils;

import java.util.function.BooleanSupplier;

public class RockCrab extends CommonCombatMethod {
    int[] ids = new int[]{100, 102, 5935, 7206, 7266, 7267};

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

    @Override
    public void onRetreat(Entity entity, BooleanSupplier waitUntil, BooleanSupplier cancel, AttributeKey key) {
        var npc = (NPC) entity;
        final int[] ticks = {4};
        if (ArrayUtils.contains(ids, npc.id())) {
            int offset = npc.id() + 1;
            npc.setEntityInteraction(null);
            System.out.println("player not found, retreating");
            npc.waitUntil(waitUntil, () ->
                Chain.noCtx().repeatingTask(1, tick -> {
                        ticks[0]--;
                        if (ticks[0] <= 0) {
                            npc.transmog(offset, true);
                            npc.clearAttribs();
                            tick.stop();
                        }
                    }).cancelWhen(() -> {
                        npc.clearAttrib(key);
                        return cancel.getAsBoolean();
                })).cancelWhen(() -> {
                npc.clearAttrib(key);
                return cancel.getAsBoolean();
            });
        }
    }
}
