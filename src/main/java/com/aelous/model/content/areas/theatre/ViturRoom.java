package com.aelous.model.content.areas.theatre;

import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;

public class ViturRoom extends PacketInteraction {

    private final int THEATRE_ENTRACE = 32653;


    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj != null && obj.getId() == THEATRE_ENTRACE) { // tob lobby 32653, 3678, 3216
            boolean insideCheck = player.getAbsX() >= 3138;
            player.lockMovement();
            Chain.bound(null).runFn(1, () -> {
                //if (!insideCheck) {
                    player.unlock();
                    player.getCombat().clearDamagers();
                    player.getVerzikViturInstance().enterInstance(player);
                //} else {
                 //   player.teleport(3678, 3216, 0);
                    //TODO log here
                //}
            });
        }
        return false;
    }

}

