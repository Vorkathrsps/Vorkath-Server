package com.cryptic.model.content.items.combine.slayerhelms;

import com.cryptic.model.content.skill.impl.slayer.SlayerConstants;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.apache.commons.lang.ArrayUtils;

public class SlayerHelmet extends PacketInteraction {
    final int[] items = new int[]{ItemIdentifiers.SPINY_HELMET, ItemIdentifiers.FACEMASK, ItemIdentifiers.ENCHANTED_GEM, ItemIdentifiers.EARMUFFS};
    final int[] masks = new int[]{ItemIdentifiers.BLACK_MASK, ItemIdentifiers.BLACK_MASK_I, ItemIdentifiers.BLACK_MASK_10};

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        if (!player.getSlayerRewards().getUnlocks().containsKey(SlayerConstants.SLAYER_HELM)) return false;
        if (player.getSkills().level(Skills.CRAFTING) < 55) {
            player.message(Color.RED.wrap("You need a Crafting level of 55 to make this."));
            return true;
        }
        boolean isUsedWith = ((Utils.isUsedWith(items, use) || Utils.isUsedWith(items, usedWith)) && (Utils.isUsedWith(masks, use) || Utils.isUsedWith(masks, usedWith)));
        if (isUsedWith) {
            for (var i : items) player.getInventory().remove(i);
            for (var i : masks) player.getInventory().remove(i);
            player.getInventory().add(new Item(ItemIdentifiers.SLAYER_HELMET));
            return true;
        }
        return false;
    }
}
