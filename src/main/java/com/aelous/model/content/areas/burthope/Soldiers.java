package com.aelous.model.content.areas.burthope;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import java.util.Arrays;
import java.util.List;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Patrick van Elderen | March, 26, 2021, 09:44
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class Soldiers extends PacketInteraction {

    private static final List<Integer> EATING_SOLDIERS = Arrays.asList(SOLDIER_4089, SOLDIER_4090, SOLDIER_4091);

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            //Sergeants
            if(npc.id() == SERGEANT || npc.id() == SERGEANT_4085) {
                player.message("The Sergeant is busy training the soldiers.");
                return true;
            }

            //Soldiers training
            if(npc.id() == SOLDIER || npc.id() == SOLDIER_4087) {
                player.message("The soldier is busy training.");
                return true;
            }

            //Soldiers eating
            for (int SOLDIERS : EATING_SOLDIERS) {
                if(npc.id() == SOLDIERS) {
                    player.message("The soldier is busy eating.");
                    return true;
                }
            }
        }
        return false;
    }
}
