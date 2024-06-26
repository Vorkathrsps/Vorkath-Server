package com.cryptic.model.content.items.combinations.trident;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class ToxicTrident extends PacketInteraction {
    int[] items = new int[]{ItemIdentifiers.TRIDENT_OF_THE_SEAS_FULL, ItemIdentifiers.MAGIC_FANG};
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (!player.getInventory().containsAll(items)) return false;
        if (ArrayUtils.contains(items, use.getId())) {
            if (ArrayUtils.contains(items, usedWith.getId())) {
                for (var i : items) player.getInventory().remove(i);
                player.getInventory().add(new Item(ItemIdentifiers.TRIDENT_OF_THE_SWAMP));
            }
        }
        return false;
    }
}
