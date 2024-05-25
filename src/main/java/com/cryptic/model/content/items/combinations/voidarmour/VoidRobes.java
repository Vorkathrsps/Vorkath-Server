package com.cryptic.model.content.items.combinations.voidarmour;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;

public class VoidRobes extends PacketInteraction {
    final int[] robeTop = new int[]{ItemIdentifiers.VOID_KNIGHT_TOP};
    final int[] robeBottom = new int[]{ItemIdentifiers.VOID_KNIGHT_ROBE};
    final int[] gloves = new int[]{ItemIdentifiers.VOID_KNIGHT_GLOVES};
    final int[] eliteRobeTop = new int[]{ItemIdentifiers.ELITE_VOID_TOP};
    final int[] eliteRobeBottom = new int[]{ItemIdentifiers.ELITE_VOID_ROBE};
    final int[] ornaments = new int[]{ItemIdentifiers.SHATTERED_RELICS_VOID_ORNAMENT_KIT};

    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        boolean isUsedWithRobeTop = ((Utils.isUsedWith(robeTop, use) || Utils.isUsedWith(robeTop, usedWith)) && (Utils.isUsedWith(ornaments, use) || Utils.isUsedWith(ornaments, usedWith)));
        boolean isUsedWithRobeBottoms = ((Utils.isUsedWith(robeBottom, use) || Utils.isUsedWith(robeBottom, usedWith)) && (Utils.isUsedWith(ornaments, use) || Utils.isUsedWith(ornaments, usedWith)));
        boolean isUsedWithEliteRobeTop = ((Utils.isUsedWith(eliteRobeTop, use) || Utils.isUsedWith(eliteRobeTop, usedWith)) && (Utils.isUsedWith(ornaments, use) || Utils.isUsedWith(ornaments, usedWith)));
        boolean isUsedWithEliteRobeBottoms = ((Utils.isUsedWith(eliteRobeBottom, use) || Utils.isUsedWith(eliteRobeBottom, usedWith)) && (Utils.isUsedWith(ornaments, use) || Utils.isUsedWith(ornaments, usedWith)));
        boolean isUsedWithGloves = ((Utils.isUsedWith(gloves, use) || Utils.isUsedWith(gloves, usedWith)) && (Utils.isUsedWith(ornaments, use) || Utils.isUsedWith(ornaments, usedWith)));
        if (isUsedWithRobeTop) {
            removeItems(player, robeTop, ornaments, ItemIdentifiers.VOID_KNIGHT_TOP_OR);
            return true;
        } else if (isUsedWithRobeBottoms) {
            removeItems(player, robeBottom, ornaments, ItemIdentifiers.VOID_KNIGHT_ROBE_OR);
            return true;
        } else if (isUsedWithGloves) {
            removeItems(player, gloves, ornaments, ItemIdentifiers.VOID_KNIGHT_GLOVES_OR);
            return true;
        } else if (isUsedWithEliteRobeTop) {
            removeItems(player, eliteRobeTop, ornaments, ItemIdentifiers.ELITE_VOID_TOP_OR);
            return true;
        } else if (isUsedWithEliteRobeBottoms) {
            removeItems(player, eliteRobeBottom, ornaments, ItemIdentifiers.ELITE_VOID_ROBE_OR);
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
