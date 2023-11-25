package com.cryptic.model.content.mechanics.death.ornaments;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import org.apache.commons.lang.ArrayUtils;

public class OrnamentPacketInteraction extends PacketInteraction {
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        for (var i : OrnamentKits.values()) {
            if (ArrayUtils.contains(i.conversion, use.getId())) {
                if (ArrayUtils.contains(i.conversion, usedWith.getId())) {
                    player.getInventory().remove(i.conversion[0]);
                    player.getInventory().remove(i.conversion[1]);
                    player.getInventory().add(new Item(i.id));
                    return true;
                }
            }
        }
        return false;
    }
}
