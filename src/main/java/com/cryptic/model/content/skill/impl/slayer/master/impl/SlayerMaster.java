package com.cryptic.model.content.skill.impl.slayer.master.impl;

import com.cryptic.model.World;
import com.cryptic.model.entity.npc.NPC;
import com.cryptic.model.entity.player.Player;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;

import static com.cryptic.cache.definitions.identifiers.NpcIdentifiers.*;

/**
 * @author Origin | April, 24, 2021, 13:46
 * 
 */
public class SlayerMaster extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if (npc.id() == KONAR_QUO_MATEN && player.skills().combatLevel() < 120) {
                player.message(Color.RED.wrap("You must have a combat level of 120 to be able to do Boss Slayer tasks."));
                return true;
            }
            if (npc.id() == NIEVE || npc.id() == KRYSTILIA || npc.id() == KONAR_QUO_MATEN) {
                npc.setPositionToFace(player.tile());
                player.getDialogueManager().start(new SlayerMasterDialogue());
                return true;
            }
        }
        if (option == 2) {
            if (npc.id() == KONAR_QUO_MATEN && player.skills().combatLevel() < 120) {
                player.message(Color.RED.wrap("You must have a combat level of 120 to be able to do Boss Slayer tasks."));
                return true;
            }
            if (npc.id() == NIEVE || npc.id() == KRYSTILIA || npc.id() == KONAR_QUO_MATEN) {
                SlayerMasterDialogue dialogue = new SlayerMasterDialogue();
                player.getDialogueManager().start(dialogue);
                npc.setPositionToFace(player.tile());
                return true;
            }
        }
        if(option == 3) {
            if (npc.id() == NIEVE || npc.id() == KRYSTILIA || npc.id() == KONAR_QUO_MATEN) {
                World.getWorld().shop(14).open(player);
                return true;
            }
        }
        if(option == 4) {
            if (npc.id() == NIEVE || npc.id() == KRYSTILIA || npc.id() == KONAR_QUO_MATEN) {
                player.getSlayerRewards().open();
                return true;
            }
        }
        return false;
    }
}
