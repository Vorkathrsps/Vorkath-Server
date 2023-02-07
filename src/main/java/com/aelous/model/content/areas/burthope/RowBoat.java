package com.aelous.model.content.areas.burthope;

import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.ROW_BOAT_27066;

/**
 * @author Patrick van Elderen | March, 26, 2021, 09:30
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class RowBoat extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if(option == 1) {
            if (object.getId() == ROW_BOAT_27066) {
                player.getDialogueManager().start(new Dialogue() {
                    @Override
                    protected void start(Object... parameters) {
                        send(DialogueType.OPTION, "Do you want to leave the island?", "Yes", "No");
                        setPhase(0);
                    }

                    @Override
                    protected void select(int option) {
                        if (isPhase(0)) {
                            if (option == 1) {
                                player.teleport(2724, 3589);
                                stop();
                            } else if (option == 2) {
                                stop();
                            }
                        }
                    }
                });
                return true;
            }
        }
        return false;
    }
}
