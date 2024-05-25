package com.cryptic.model.entity.npc.droptables.impl.raids;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.player.Player;

/**
 * @Author Origin
 * @Since October 30, 2021
 */
public class VespulaDroptable implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var party = killer.raidsParty;

        if (party != null) {
            var currentKills = party.getKills();
            party.setKills(currentKills + 1);
            party.teamMessage("<col=ef20ff>"+npc.def().name+" has been defeated!");

            //Progress to the next stage
            if (party.getKills() == 1) {
                party.setRaidStage(6);
                party.teamMessage("<col=ef20ff>You may now progress to the next room!");
                party.setKills(0);//Reset kills back to 0
            }
        }
    }
}
