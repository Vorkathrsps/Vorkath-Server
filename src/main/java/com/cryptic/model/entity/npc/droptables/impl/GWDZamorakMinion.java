package com.cryptic.model.entity.npc.droptables.impl;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.player.Player;

import static com.cryptic.model.entity.attributes.AttributeKey.GWD_ZAMORAK_KC;

/**
 * @author Origin | April, 29, 2021, 14:24
 * 
 */
public class GWDZamorakMinion implements Droptable {
    @Override
    public void reward(NPC npc, Player killer) {
        var current = killer.<Integer>getAttribOr(GWD_ZAMORAK_KC, 0) + 1;
        if (current < 2000) {
            killer.putAttrib(GWD_ZAMORAK_KC, current);
        }
    }
}
