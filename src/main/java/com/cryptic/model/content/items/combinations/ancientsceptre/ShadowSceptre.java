package com.cryptic.model.content.items.combinations.ancientsceptre;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class ShadowSceptre extends PacketInteraction {
    int[] items = new int[]{ItemIdentifiers.SHADOW_QUARTZ, ItemIdentifiers.ANCIENT_SCEPTRE};
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (!player.getInventory().containsAll(items)) return false;
        if (ArrayUtils.contains(items, use.getId())) {
            if (ArrayUtils.contains(items, usedWith.getId())) {
                for (var i : items) player.getInventory().remove(i);
                player.getInventory().add(new Item(ItemIdentifiers.SHADOW_ANCIENT_SCEPTRE_28266));
            }
        }
        return false;
    }
}
