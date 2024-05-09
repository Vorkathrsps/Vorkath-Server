package com.cryptic.model.content.items;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.chainedwork.Chain;

public class GrandSeedPod extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (item.getId() == 9469) {
            if (option == 1) {
                if (Teleports.canTeleport(player, true, TeleportType.ABOVE_20_WILD)) {
                    player.stopActions(true);
                    player.lockMovement();
                    player.animate(4544);
                    player.graphic(767);
                    Chain.noCtx().runFn(4, () -> {
                        player.looks().hideLooks(true);
                    }).then(3, () -> {
                        player.teleport(new Tile(3099, 3506));
                        player.animate(4546);
                        player.graphic(769);
                    }).then(2, () -> {
                        player.looks().hideLooks(false);
                        player.unlock();
                    });
                    return true;
                }
            }
        }

        return false;
    }
}
