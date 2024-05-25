package com.cryptic.model.content.areas.burthope.dialogue;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.cache.definitions.identifiers.NpcIdentifiers;

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
