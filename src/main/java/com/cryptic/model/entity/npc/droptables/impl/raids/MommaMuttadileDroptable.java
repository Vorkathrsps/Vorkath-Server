package com.cryptic.model.entity.npc.droptables.impl.raids;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.npc.droptables.Droptable;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.ObjectManager;

/**
 * @Author Origin
 * @Since October 30, 2021
 */
public class MommaMuttadileDroptable implements Droptable {

    @Override
    public void reward(NPC npc, Player killer) {
        var party = killer.raidsParty;

        if (party != null) {
            var currentKills = party.getKills();
            party.setKills(currentKills + 1);
            party.teamMessage("<col=ef20ff>"+npc.def().name+" has been defeated!");

            //Progress to the next stage
            if (party.getKills() == 2) {
                party.setRaidStage(5);
                party.teamMessage("<col=ef20ff>You may now progress to the next room!");
                party.setKills(0);//Reset kills back to 0

                //Delete the meat tree
                ObjectManager.removeObj(party.getMeatTree());
            }
        }
    }
}
