package com.aelous.model.entity.npc.droptables.impl.raids;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.player.Player;

/**
 * @Author Origin
 * @Since October 27, 2021
 */
public class LordVoldemortDroptable implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var party = killer.raidsParty;

        if (party != null) {
            if (party.getLeader().getRaids() != null)
                party.getLeader().getRaids().complete(party);
        }
    }
}
