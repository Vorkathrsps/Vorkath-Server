package com.aelous.model.content.skill.impl.slayer.master.impl;

import com.aelous.model.World;
import com.aelous.model.entity.npc.NPC;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

import static com.aelous.cache.definitions.identifiers.NpcIdentifiers.THORODIN_5526;

/**
 * @author Patrick van Elderen | April, 24, 2021, 13:46
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class SlayerMaster extends PacketInteraction {

    @Override
    public boolean handleNpcInteraction(Player player, NPC npc, int option) {
        if(option == 1) {
            if (npc.id() == THORODIN_5526) {
                npc.setPositionToFace(player.tile());
                player.getDialogueManager().start(new SlayerMasterDialogue());
                return true;
            }
        }
        if(option == 2) {
            if (npc.id() == THORODIN_5526) {
                World.getWorld().shop(14).open(player);
                return true;
            }
        }
        if(option == 3) {
            if (npc.id() == THORODIN_5526) {
                player.getSlayerRewards().open();
                return true;
            }
        }
        return false;
    }
}
