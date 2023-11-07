package com.cryptic.model.entity.npc.droptables.impl;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.model.entity.attributes.AttributeKey.GWD_SARADOMIN_KC;

/**
 * @author Origin | April, 29, 2021, 14:23
 * 
 */
public class GWDSaradominMinion implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var current = killer.<Integer>getAttribOr(GWD_SARADOMIN_KC, 0) + 1;
        if (current < 2000) {
            killer.putAttrib(GWD_SARADOMIN_KC, current);
        }
    }
}
