package com.cryptic.model.entity.npc.droptables.impl.raids;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.player.Player;

/**
 * @author Origin | May, 10, 2021, 18:36
 * 
 */
public class CentaursDroptable implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var party = killer.raidsParty;

        if (party != null) {
            var currentKills = party.getKills();
            party.setKills(currentKills + 1);
            party.teamMessage("<col=ef20ff>" + killer.getUsername() + " has killed a " + npc.def().name + ".");
            //System.out.println(party.getKills());

            //Progress to the next stage
            if (party.getKills() == 8) {
                party.setRaidStage(3);
                party.teamMessage("<col=ef20ff>You may now progress to the next room!");
                party.setKills(0);//Reset kills back to 0
            }
        }
    }
}
