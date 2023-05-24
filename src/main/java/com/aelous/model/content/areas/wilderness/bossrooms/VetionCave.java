package com.aelous.model.content.areas.wilderness.bossrooms;

import com.aelous.model.content.areas.wilderness.bossrooms.memorial.MemorialDialogue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;

public class VetionCave extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        switch (obj.getId()) {
            case 46996 -> {
                if (option == 1) {
                    player.getDialogueManager().start(new MemorialDialogue());
                    return true;
                }
            }
        }
        return false;
    }
}
