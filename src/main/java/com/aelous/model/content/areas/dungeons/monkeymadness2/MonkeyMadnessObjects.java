package com.aelous.model.content.areas.dungeons.monkeymadness2;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

public class MonkeyMadnessObjects extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            // Cave exit:
            if (obj.getId() == ROPE_28687) {
                Chain.bound(null).runFn(1, () -> player.animate(828)).then(1, () -> player.teleport(2026, 5611));
                return true;
            }

            // Cave entrance:
            if (obj.getId() == CAVERN_ENTRANCE_28686) {
                Chain.bound(null).runFn(1, () -> {
                    player.message("You enter the cavern beneath the crash site.");
                    player.message("Why would you want to go in there?");
                }).then(1, () -> {
                    player.teleport(2129, 5646);
                });
                return true;
            }

            // Gnome stronghold exit
            if (obj.getId() == OPENING_28656) {
                if (player.tile().y >= 5568) {
                    player.teleport(2435, 3519);
                } else {
                    player.getDialogueManager().start(new Dialogue() {
                        @Override
                        protected void start(Object... parameters) {
                            send(DialogueType.OPTION, "Leave the Gnome Stronghold?", "Yes.", "No.");
                        }

                        @Override
                        public void select(int option) {
                            if (option == 1) {
                                player.teleport(1987, 5568);
                            } else if (option == 2) {
                                stop();
                            }
                        }
                    });
                }
                return true;
            }
        }
        return false;
    }
}
