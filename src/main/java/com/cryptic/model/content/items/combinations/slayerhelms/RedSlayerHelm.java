package com.cryptic.model.content.items.combinations.slayerhelms;

import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class RedSlayerHelm extends PacketInteraction {
    int[] items = new int[]{ItemIdentifiers.SLAYER_HELMET, ItemIdentifiers.ABYSSAL_HEAD};
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (!player.getInventory().containsAll(items)) return false;
        if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.UNHOLY_HELMET)) return false;
        if (ArrayUtils.contains(items, use.getId())) {
            if (ArrayUtils.contains(items, usedWith.getId())) {
                for (var i : items) player.getInventory().remove(i);
                player.getInventory().add(new Item(ItemIdentifiers.RED_SLAYER_HELMET));
            }
        }
        return false;
    }
}
