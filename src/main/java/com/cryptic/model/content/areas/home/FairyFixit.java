package com.cryptic.model.content.areas.home;

import com.cryptic.model.content.areas.home.dialogue.FairyFixitD;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.FAIRY_FIXIT_7333;

/**
 * @author Origin | April, 23, 2021, 13:52
 * 
 */
public class FairyFixit extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if(npc.id() == FAIRY_FIXIT_7333) {
                player.getDialogueManager().start(new FairyFixitD());
                return true;
            }
        }
        return false;
    }
}
