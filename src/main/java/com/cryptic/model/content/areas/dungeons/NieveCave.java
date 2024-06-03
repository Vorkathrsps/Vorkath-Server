package com.cryptic.model.content.areas.dungeons;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

import static com.cryptic.cache.definitions.identifiers.ObjectIdentifiers.CREVICE_535;

public class NieveCave extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if(option == 1) {
            if(obj.getId() == CREVICE_535) {
                player.teleport(2376, 9452);
                return true;
            } else  if(obj.getId() == 536) {
                player.teleport(2379, 9452);
                return true;
            }
        } else if(option == 2) {
            if(obj.getId() == CREVICE_535) {
                int count = 0;
                for (Player p : World.getWorld().getPlayers()) {
                    if (p != null && p.tile().inArea(2344, 9434, 2376, 9462))
                        count++;
                    String pluralOr = count == 1 ? "" : "s";
                    player.getDialogueManager().sendStatement("There are currently "+count+" player"+pluralOr+" in the cave.");
                }
                return true;
            }
        }
        return false;
    }
}
