package com.cryptic.model.content.areas.burthope.warriors_guild;

import com.cryptic.model.content.areas.burthope.warriors_guild.dialogue.*;
import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Origin | April, 14, 2021, 18:30
 * 
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
