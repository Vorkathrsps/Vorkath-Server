package com.aelous.model.content.areas.wilderness.mageArena;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.masks.impl.graphics.GraphicHeight;
import com.aelous.model.entity.player.Player;
import com.aelous.model.map.object.GameObject;
import com.aelous.model.map.position.Tile;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.chainedwork.Chain;
import com.aelous.utility.timers.TimerKey;

public class MageArenaLever extends PacketInteraction {

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (option == 1) {
            //Into the arena
            if (obj.getId() == 9706) {
                //Check to see if the player is teleblocked
                if (player.getTimers().has(TimerKey.TELEBLOCK) || player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                    player.teleblockMessage();
                    return true;
                }

                player.lockDelayDamage();
                Chain.bound(null).runFn(1, () -> {
                    player.animate(2710);
                    player.message("You pull the lever...");
                }).then(2, () -> {
                    player.animate(714);
                    player.graphic(111, GraphicHeight.HIGH, 0);
                }).then(4, () -> {
                    player.teleport(new Tile(3105, 3951, 0));
                    player.animate(-1);
                    player.unlock();
                    player.putAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY, true);
                    player.message("...and get teleported into the arena!");
                });
                return true;
            }
            //Inside magebank.. to outside
            if (obj.getId() == 9707) {
                //Check to see if the player is teleblocked
                if (!player.getTimers().has(TimerKey.TELEBLOCK) || !player.getTimers().has(TimerKey.SPECIAL_TELEBLOCK)) {
                    player.lockDelayDamage();
                    Chain.bound(null).runFn(1, () -> {
                        player.animate(2710);
                        player.message("You pull the lever...");
                    }).then(2, () -> {
                        player.animate(714);
                        player.graphic(111, GraphicHeight.HIGH, 0);
                    }).then(4, () -> {
                        player.teleport(new Tile(3105, 3956, 0));
                        player.animate(-1);
                        player.unlock();
                        player.putAttrib(AttributeKey.MAGEBANK_MAGIC_ONLY, false);
                        player.message("...and get teleported out of the arena!");
                    });
                } else {
                    player.teleblockMessage();
                }
                return true;
            }
        }
        return false;
    }
}
