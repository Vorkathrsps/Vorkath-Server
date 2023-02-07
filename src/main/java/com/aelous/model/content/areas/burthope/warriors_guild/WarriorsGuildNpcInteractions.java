package com.aelous.model.content.areas.burthope.warriors_guild;

import com.aelous.model.content.areas.burthope.warriors_guild.dialogue.*;
import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Patrick van Elderen | April, 14, 2021, 18:30
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class WarriorsGuildNpcInteractions extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if(npc.id() == AJJAT) {
                player.getDialogueManager().start(new Ajjat());
                return true;
            }
            if(npc.id() == ANTON) {
                player.getDialogueManager().start(new Anton());
                return true;
            }
            if (npc.id() == GHOMMAL) {
                player.getDialogueManager().start(new Ghommal());
                return true;
            }
            if (npc.id() == LIDIO) {
                player.getDialogueManager().start(new Lidio());
                return true;
            }
            if (npc.id() == LILLY) {
                player.getDialogueManager().start(new Lilly());
                return true;
            }
            if (npc.id() == SHANOMI) {
                player.getDialogueManager().start(new Shanomi());
                return true;
            }
        }

        if(option == 2) {
            if(npc.id() == ANTON) {
                World.getWorld().shop(24).open(player);
                return true;
            }
            if(npc.id() == LIDIO) {
                World.getWorld().shop(25).open(player);
                return true;
            }
            if(npc.id() == LILLY) {
                World.getWorld().shop(26).open(player);
                return true;
            }
        }
        return false;
    }
}
