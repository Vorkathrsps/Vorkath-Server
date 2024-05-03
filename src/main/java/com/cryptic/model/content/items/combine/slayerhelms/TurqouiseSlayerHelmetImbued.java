package com.cryptic.model.content.items.combine.slayerhelms;

import com.cryptic.cache.definitions.ItemDefinition;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class TurqouiseSlayerHelmetImbued extends PacketInteraction {
    int[] items = new int[]{ItemIdentifiers.TURQUOISE_SLAYER_HELMET, ItemIdentifiers.SCROLL_OF_REDIRECTION};
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (!player.getInventory().containsAll(items)) return false;
        if (ArrayUtils.contains(items, use.getId())) {
            if (ArrayUtils.contains(items, usedWith.getId())) {
                for (var i : items) player.getInventory().remove(i);
                player.getInventory().add(ItemIdentifiers.TURQUOISE_SLAYER_HELMET_I);
                var def = ItemDefinition.cached.get(ItemIdentifiers.TURQUOISE_SLAYER_HELMET_I);
                player.doubleItemStatement("You've created a " + def.name + ".", ItemIdentifiers.SCROLL_OF_REDIRECTION, ItemIdentifiers.TURQUOISE_SLAYER_HELMET_I);
                return true;
            }
        }
        return false;
    }
}
