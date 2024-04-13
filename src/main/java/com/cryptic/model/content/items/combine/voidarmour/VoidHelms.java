package com.cryptic.model.content.items.combine.voidarmour;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

public class VoidHelms extends PacketInteraction {
    final int[] mageHelm = new int[]{ItemIdentifiers.VOID_MAGE_HELM};
    final int[] rangeHelm = new int[]{ItemIdentifiers.VOID_RANGER_HELM};
    final int[] meleeHelm = new int[]{ItemIdentifiers.VOID_MELEE_HELM};
    final int[] ornaments = new int[]{ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT};

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        boolean isUsedWithMagicHelm = ((Utils.isUsedWith(mageHelm, use) || Utils.isUsedWith(mageHelm, usedWith)) && (Utils.isUsedWith(ornaments, use) || Utils.isUsedWith(ornaments, usedWith)));
        boolean isUsedWithMeleeHelm = ((Utils.isUsedWith(meleeHelm, use) || Utils.isUsedWith(meleeHelm, usedWith)) && (Utils.isUsedWith(ornaments, use) || Utils.isUsedWith(ornaments, usedWith)));
        boolean isUsedWithRangeHelm = ((Utils.isUsedWith(rangeHelm, use) || Utils.isUsedWith(rangeHelm, usedWith)) && (Utils.isUsedWith(ornaments, use) || Utils.isUsedWith(ornaments, usedWith)));
        if (isUsedWithMagicHelm) {
            removeItems(player, mageHelm, ornaments, ItemIdentifiers.VOID_MAGE_HELM_OR);
            return true;
        } else if (isUsedWithMeleeHelm) {
            removeItems(player, meleeHelm, ornaments, ItemIdentifiers.VOID_MELEE_HELM_OR);
            return true;
        } else if (isUsedWithRangeHelm) {
            removeItems(player, rangeHelm, ornaments, ItemIdentifiers.VOID_RANGER_HELM_OR);
            return true;
        }
        return false;
    }

    final void removeItems(Player player, int[] items, int[] ornaments, int add) {
        for (var i : items) player.getInventory().remove(i);
        for (var i : ornaments) player.getInventory().remove(i);
        player.getInventory().add(new Item(add));
    }
}
