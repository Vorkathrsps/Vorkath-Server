package com.cryptic.model.entity.combat.method.impl.npcs.bosses.theduke.interactions;

import com.cryptic.model.entity.combat.method.impl.npcs.bosses.theduke.instance.TheDukeInstance;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Dialogue;
import com.cryptic.model.inter.dialogue.DialogueType;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class DrBanikan extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (npc.id() == 12293) {
            player.getDialogueManager().start(new Dialogue() {
                @Override
                protected void start(Object... parameters) {
                    sendOption("Would you like to fight Duke Sucellus?", "Yes.", "No.");
                    setPhase(0);
                }

                @Override
                protected void select(int option) {
                    if (isPhase(0)) {
                        if (option == 1) {
                            TheDukeInstance instance = new TheDukeInstance(player);
                            instance.build();
                            stop();
                            return;
                        }

                        if (option == 2) {
                            stop();
                        }
                    }
                }
            });
            return true;
        }
        return false;
    }
}
