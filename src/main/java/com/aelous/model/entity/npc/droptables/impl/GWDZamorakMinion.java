package com.aelous.model.entity.npc.droptables.impl;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.player.Player;

import static com.aelous.model.entity.attributes.AttributeKey.GWD_ZAMORAK_KC;

/**
 * @author Patrick van Elderen | April, 29, 2021, 14:24
 * @see <a href="https://github.com/PVE95">Github profile</a>
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
