package com.aelous.model.entity.npc.droptables.impl.raids;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.npc.droptables.Droptable;
import com.aelous.model.entity.player.Player;

/**
 * @author Patrick van Elderen | May, 13, 2021, 11:57
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class HungarianHorntailDroptable implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var party = killer.raidsParty;

        if (party != null) {
            var currentKills = party.getKills();
            party.setKills(currentKills + 1);
            party.teamMessage("<col=ef20ff>" + killer.getUsername() + " has killed a " + npc.def().name + ".");
            System.out.println(party.getKills());

            //Progress to the next stage
            if (party.getKills() == 3) {
                party.setRaidStage(6);
                party.teamMessage("<col=ef20ff>You may now progress to the next room!");
                party.setKills(0);//Reset kills back to 0
            }
        }
    }
}
