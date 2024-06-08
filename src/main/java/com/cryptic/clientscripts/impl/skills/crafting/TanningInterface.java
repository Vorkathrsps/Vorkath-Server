package com.cryptic.clientscripts.impl.skills.crafting;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.InterfaceBuilder;
import com.cryptic.clientscripts.util.CombinedId;
import com.cryptic.clientscripts.util.JagexColor;
import com.cryptic.interfaces.GameInterface;
import com.cryptic.interfaces.PaneType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.ItemIdentifiers;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public class TanningInterface extends InterfaceBuilder {

    @Override
    public GameInterface gameInterface() {
        return GameInterface.TANNING_INTERFACE;
    }

    @Override
    public void beforeOpen(Player player) {
        JagexColor LEATHER_COLOR = player.getInventory().contains(ItemIdentifiers.COWHIDE) ? JagexColor.LIGHT_GREEN : JagexColor.RED, SNAKESKIN_COLOR = player.getInventory().contains(ItemIdentifiers.SNAKESKIN) ? JagexColor.LIGHT_GREEN : JagexColor.RED, GREEN_DHIDE_COLOR = player.getInventory().contains(ItemIdentifiers.GREEN_DRAGONHIDE) ? JagexColor.LIGHT_GREEN : JagexColor.RED, BLUE_DHIDE_COLOR = player.getInventory().contains(ItemIdentifiers.BLUE_DRAGONHIDE) ? JagexColor.LIGHT_GREEN : JagexColor.RED, RED_DHIDE_COLOR = player.getInventory().contains(ItemIdentifiers.RED_DRAGONHIDE) ? JagexColor.LIGHT_GREEN : JagexColor.RED, BLACK_DHIDE_COLOR = player.getInventory().contains(ItemIdentifiers.BLACK_DRAGONHIDE) ? JagexColor.LIGHT_GREEN : JagexColor.RED;
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_EMPTY, "");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_SOFT_LEATHER_1, "Soft leather");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_1_COINS, "1 coins");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_SOFT_LEATHER_2, "Hard leather");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_3_COINS, "3 coins");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_SNAKESKIN_1, "Snakeskin");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_20_COINS_1, "20 coins");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_SNAKESKIN_2, "Snakeskin");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_15_COINS, "15 coins");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_GREEN_DHIDE, "Green d'hide");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_20_COINS_2, "20 coins");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_BLUE_DHIDE, "Blue d'hide");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_20_COINS_3, "20 coins");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_RED_DHIDE, "Red d'hide");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_20_COINS_4, "20 coins");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_BLACK_DHIDE, "Black d'hide");
        player.getPacketSender().setComponentText(ComponentID.COMPONENT_TEXT_20_COINS_5, "20 coins");
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_1), LEATHER_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_2), LEATHER_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_3), LEATHER_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_4), LEATHER_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_5), SNAKESKIN_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_6), SNAKESKIN_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_7), SNAKESKIN_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_8), SNAKESKIN_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_9), GREEN_DHIDE_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_10), GREEN_DHIDE_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_11), BLUE_DHIDE_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_12), BLUE_DHIDE_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_13), RED_DHIDE_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_14), RED_DHIDE_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_15), BLACK_DHIDE_COLOR);
        player.getPacketSender().setTextComponentColor(new CombinedId(ComponentID.TANNING_STRING_RGB_16), BLACK_DHIDE_COLOR);
        player.getPacketSender().setItemMessage(ComponentID.SOFT_LEATHER_ITEM, ItemIdentifiers.LEATHER, 250);
        player.getPacketSender().setItemMessage(ComponentID.HARD_LEATHER_ITEM, ItemIdentifiers.HARD_LEATHER, 250);
        player.getPacketSender().setItemMessage(ComponentID.SNAKESKIN_ITEM_1, ItemIdentifiers.SNAKESKIN, 250);
        player.getPacketSender().setItemMessage(ComponentID.SNAKESKIN_ITEM_2, ItemIdentifiers.SNAKESKIN, 250);
        player.getPacketSender().setItemMessage(ComponentID.GREEN_DHIDE_ITEM, ItemIdentifiers.GREEN_DRAGONHIDE, 250);
        player.getPacketSender().setItemMessage(ComponentID.BLUE_DHIDE_ITEM, ItemIdentifiers.BLUE_DRAGONHIDE, 250);
        player.getPacketSender().setItemMessage(ComponentID.RED_DHIDE_ITEM, ItemIdentifiers.RED_DRAGONHIDE, 250);
        player.getPacketSender().setItemMessage(ComponentID.BLACK_DHIDE_ITEM, ItemIdentifiers.BLACK_DRAGONHIDE, 250);
        player.getPacketSender().sendSubInterfaceModal(gameInterface().getId(), 40, PaneType.FIXED);
    }


    @Override
    public void onButton(Player player, int button, int option, int slot, int itemId) {

    }
}
