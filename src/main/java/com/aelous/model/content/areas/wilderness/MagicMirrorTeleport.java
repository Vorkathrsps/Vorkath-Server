package com.aelous.model.content.areas.wilderness;

import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

public class MagicMirrorTeleport extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            //Into the arena
            if (obj.getId() == 34683) {
                //Check to see if the player is teleblocked
                if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                    player.teleblockMessage();
                    return true;
                }

                player.lockDelayDamage();
                Chain.bound(null).runFn(1, () -> {
                    player.animate(2710);
                    player.message("You grab onto the mirror...");
                }).then(2, () -> {
                    player.animate(714);
                    player.graphic(111, GraphicHeight.HIGH, 0);
                }).then(4, () -> {
                    player.teleport(new Tile(3094, 3503, 0));
                    player.animate(-1);
                    player.unlock();
                    player.message("...and get taken by a magical force to home!");
                });
                return true;
            }
        }
        return false;
    }
}
