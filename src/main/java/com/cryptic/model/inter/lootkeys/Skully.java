package com.cryptic.model.inter.lootkeys;

import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.SKULLY;

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
