package com.aelous.model.content.areas.edgevile;

import com.aelous.model.content.areas.edgevile.dialogue.PaymentManagerDialogue;
import com.aelous.model.World;
import com.aelous.model.inter.dialogue.Dialogue;
import com.aelous.model.inter.dialogue.DialogueType;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.WISE_OLD_MAN;

/**
 * @author Patrick van Elderen | April, 22, 2021, 11:46
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class WiseOldMan extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if (option == 1) {
            if (npc.id() == WISE_OLD_MAN) {
                player.getDialogueManager().start(new PaymentManagerDialogue());
                return true;
            }
        }
        if (option == 2) {
            if (npc.id() == WISE_OLD_MAN) {
                openStore(player);
                return true;
            }
        }
        if (option == 3) {

        }
        return false;
    }

    private void openStore(Player player) {
        player.getDialogueManager().start(new Dialogue() {
            @Override
            protected void start(Object... parameters) {
                send(DialogueType.OPTION, DEFAULT_OPTION_TITLE, "Open webstore.", "Donator store.", "Nevermind.");
                setPhase(0);
            }

            @Override
            protected void select(int option) {
                if (option == 1) {
                    player.getPacketSender().sendURL("https://aelous.net/store/");
                }
                if (option == 2) {
                    World.getWorld().shop(43).open(player);
                    player.getPacketSender().sendConfig(1125, 1);
                    player.getPacketSender().sendConfig(1126, 0);
                    player.getPacketSender().sendConfig(1127, 0);
                }
            }
        });
    }
}
