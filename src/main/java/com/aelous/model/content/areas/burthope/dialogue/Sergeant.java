package com.aelous.model.content.areas.burthope.dialogue;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.cache.definitions.identifiers.NpcIdentifiers;

/**
 * @author PVE
 * @Since juli 19, 2020
 */
public class Sergeant extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        //Sergeants
        if (npc.id() == NpcIdentifiers.SERGEANT || npc.id() == NpcIdentifiers.SERGEANT_4085) {
            player.message("The Sergeant is busy training the soldiers.");
            return true;
        }
        return false;
    }
}
