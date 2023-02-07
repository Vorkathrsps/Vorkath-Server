package com.aelous.model.content.areas.home;

import com.aelous.model.content.areas.home.dialogue.FairyFixitD;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.FAIRY_FIXIT_7333;

/**
 * @author Patrick van Elderen | April, 23, 2021, 13:52
 * @see <a href="https://github.com/PVE95">Github profile</a>
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
