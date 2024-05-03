package com.cryptic.model.content.items.combine.slayerhelms;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class RedSlayerHelmetImbued  extends PacketInteraction {
    int[] items = new int[]{ItemIdentifiers.RED_SLAYER_HELMET, ItemIdentifiers.SCROLL_OF_REDIRECTION};
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (!player.getInventory().containsAll(items)) return false;
        if (ArrayUtils.contains(items, use.getId())) {
            if (ArrayUtils.contains(items, usedWith.getId())) {
                player.getInventory().remove(ItemIdentifiers.SCROLL_OF_REDIRECTION, 1);
                player.getInventory().remove(ItemIdentifiers.RED_SLAYER_HELMET);
                player.getInventory().add(ItemIdentifiers.RED_SLAYER_HELMET_I);
                var def = ItemDefinition.cached.get(ItemIdentifiers.RED_SLAYER_HELMET_I);
                player.doubleItemStatement("You've created a " + def.name + ".", ItemIdentifiers.SCROLL_OF_REDIRECTION, ItemIdentifiers.RED_SLAYER_HELMET_I);
                return true;
            }
        }
        return false;
    }
}
