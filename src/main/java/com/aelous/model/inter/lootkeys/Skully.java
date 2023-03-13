package com.aelous.model.inter.lootkeys;

import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.SKULLY;

public class Skully extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if(npc.id() == SKULLY) {
                player.getDialogueManager().start(new SkullyD());
                return true;
            }
        }
        if(option == 2) {
            if(npc.id() == SKULLY) {
                player.getDialogueManager().start(new SkullyValueD());
                return true;
            }
        }
        if(option == 3) {
            if(npc.id() == SKULLY) {
                player.getDialogueManager().start(new SkullySettingsD());
                return true;
            }
        }
        return false;
    }
}
