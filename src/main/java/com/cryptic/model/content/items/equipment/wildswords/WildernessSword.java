package com.cryptic.model.content.items.equipment.wildswords;

import com.cryptic.model.content.teleport.TeleportType;
import com.cryptic.model.content.teleport.Teleports;
import com.cryptic.model.entity.masks.impl.animations.Animation;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.Tile;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.chainedwork.Chain;

public class WildernessSword extends PacketInteraction {
    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (option == 3) {
            if (item.getId() == ItemIdentifiers.WILDERNESS_SWORD_3) {
                if (Teleports.canTeleport(player, true, TeleportType.ABOVE_20_WILD)) {
                    player.lockMovement();
                    player.animate(3872);
                    player.graphic(283);
                    Chain.noCtx().runFn(4, () -> {
                       player.teleport(new Tile(2998, 3913));
                       player.animate(Animation.DEFAULT_RESET_ANIMATION);
                       player.unlock();
                    });
                }
                return true;
            }
        }
        return false;
    }
}
