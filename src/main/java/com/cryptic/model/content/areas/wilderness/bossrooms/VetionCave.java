package com.cryptic.model.content.areas.wilderness.bossrooms;

import com.cryptic.model.content.areas.wilderness.bossrooms.memorial.MemorialDialogue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

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
