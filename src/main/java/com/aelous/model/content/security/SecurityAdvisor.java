package com.aelous.model.content.security;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SECURITY_GUARD;

/**
 * @author Patrick van Elderen | April, 29, 2021, 18:16
 * @see <a href="https://github.com/PVE95">Github profile</a>
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
