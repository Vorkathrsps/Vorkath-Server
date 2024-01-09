package com.cryptic.model.entity.combat.method.impl.npcs.aggression;

import com.cryptic.model.entity.Entity;
import com.cryptic.model.entity.npc.AggressionCheck;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import org.apache.commons.lang.ArrayUtils;

public class RockCrabsAggro implements AggressionCheck {
    int[] ids = new int[]{101, 103, 5936, 7207, 7267};
    @Override
    public boolean shouldAgro(Entity entity, Entity victim) {
        NPC npc = (NPC) entity;
        if (victim instanceof Player player) {
            if (ArrayUtils.contains(ids, npc.id())) {
                if (entity.tile().distance(player.tile()) == 1) {
                    int offset = npc.id() - 1;
                    npc.transmog(offset, true);
                    return true;
                }
            }
        }
        return false;
    }
}
