package com.aelous.model.content.areas.dungeons.gnome_stronghold;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

import static com.aelous.cache.definitions.identifiers.ObjectIdentifiers.*;

public class GnomeEntrance extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            if (obj.getId() == CAVE_26709) {
                player.lock();
                player.animate(2796);
                Chain.bound(null).runFn(2, () -> {
                    player.resetAnimation();
                    player.teleport(2429, 9824, 0);
                    player.unlock();
                });
                return true;
            }
            if (obj.getId() == TUNNEL_27257 || obj.getId() == TUNNEL_27258) {
                player.lock();
                player.animate(2796);
                Chain.bound(null).runFn(2, () -> {
                    player.resetAnimation();
                    player.teleport(2430, 3424, 0);
                    player.unlock();
                });
                return true;
            }
        }
        return false;
    }
}
