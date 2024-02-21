package com.cryptic.model.entity.combat.method.impl.npcs.bosses.scurrius;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

public class ScurriusInteractions extends PacketInteraction {
    @Override
    public boolean handleObjectInteraction(Player player, GameObject object, int option) {
        if (object.getId() == 14203) {
            player.lock();
            player.sendPrivateSound(7655, 0);
            player.animate(828);
            player.getPacketSender().sendScreenFade("", 1, 1);
            Chain.noCtx().runFn(2, () -> {
                player.teleport(new Tile(3290, 9868));
                player.unlock();
            });
            return true;
        } else if (object.getId() == 14204) {
            player.lock();
            player.sendPrivateSound(7655, 0);
            player.animate(828);
            player.getPacketSender().sendScreenFade("", 1, 1);
            Chain.noCtx().runFn(2, () -> {
                player.teleport(new Tile(3281, 9867));
                player.unlock();
            });
        }
        return false;
    }
}
