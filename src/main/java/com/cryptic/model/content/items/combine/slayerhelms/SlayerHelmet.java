package com.cryptic.model.content.items.combine.slayerhelms;

import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import org.apache.commons.lang.ArrayUtils;

public class SlayerHelmet extends PacketInteraction {
    int[] items = new int[]{ItemIdentifiers.SPINY_HELMET, ItemIdentifiers.FACEMASK, ItemIdentifiers.ENCHANTED_GEM, ItemIdentifiers.EARMUFFS, ItemIdentifiers.BLACK_MASK };
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.SLAYER_HELM)) return false;
        if (player.getSkills().level(Skills.CRAFTING) < 55) {
            player.message(Color.RED.wrap("You need a Crafting level of 55 to make this."));
            return true;
        }
        if (ArrayUtils.contains(items, use.getId())) {
            if (ArrayUtils.contains(items, usedWith.getId())) {
                if (!player.getInventory().containsAll(items)) return false;
                for (var i : items) player.getInventory().remove(i);
                player.getInventory().add(new Item(ItemIdentifiers.SLAYER_HELMET));
                return true;
            }
        }
        return false;
    }
}
