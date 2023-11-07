package com.cryptic.model.content.security;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.SECURITY_GUARD;

/**
 * @author Origin | April, 29, 2021, 18:16
 * 
 */
public class SecurityAdvisor extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if(npc.id() == SECURITY_GUARD) {
                player.getDialogueManager().start(new SecurityAdvisorDialogue());
                return true;
            }
        }
        return false;
    }
}
