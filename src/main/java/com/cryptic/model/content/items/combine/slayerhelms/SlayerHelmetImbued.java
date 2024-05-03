package com.cryptic.model.content.items.combine.slayerhelms;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class SlayerHelmetImbued extends PacketInteraction {
    int[] items = new int[]{ItemIdentifiers.SLAYER_HELMET, ItemIdentifiers.SCROLL_OF_REDIRECTION};
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (!player.getInventory().containsAll(items)) return false;
        if (ArrayUtils.contains(items, use.getId())) {
            if (ArrayUtils.contains(items, usedWith.getId())) {
                player.getInventory().remove(ItemIdentifiers.SLAYER_HELMET);
                player.getInventory().add(ItemIdentifiers.SLAYER_HELMET_I);
                return true;
            }
        }
        return false;
    }
}
