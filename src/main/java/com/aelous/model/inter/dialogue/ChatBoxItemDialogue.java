package com.aelous.model.inter.dialogue;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * juni 17, 2020
 */
public abstract class ChatBoxItemDialogue {

    private final Player player;

    protected ChatBoxItemDialogue(Player player) {
        this.player = player;
    }

    public static void sendInterface(Player player, int interfaceId, int zoom, Item item) {
        sendInterface(player, interfaceId, zoom, item.getId());
    }

    public static void sendInterface(Player player, int interfaceId, int zoom, int item) {
        ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, item);
        player.getPacketSender().sendString(2799, "" + def.name);
        player.getPacketSender().sendInterfaceModel(interfaceId, zoom, item);
        player.getPacketSender().sendChatboxInterface(4429);
    }

    public boolean clickButton(int button) {
        switch (button) {
            /* Option 1 */
            case 2799, 8909 -> {
                firstOptionClick(player);
                return true;
            }
            /* Option 5 */
            case 2798 -> {
                secondOptionClick(player);
                return true;
            }
            /* Option x */
            case 1748 -> {
                thirdOptionClick(player);
                return true;
            }
            /* Option all */
            case 1747 -> {
                fourthOptionClick(player);
                return true;
            }
        }
        return false;
    }

    public abstract void firstOption(Player player);

    public abstract void secondOption(Player player);

    public abstract void thirdOption(Player player);

    public abstract void fourthOption(Player player);

    private void firstOptionClick(Player player) {
        player.getInterfaceManager().close();
        firstOption(player);
    }

    private void secondOptionClick(Player player) {
        player.getInterfaceManager().close();
        secondOption(player);
    }

    private void thirdOptionClick(Player player) {
        player.getInterfaceManager().close();
        thirdOption(player);
    }

    private void fourthOptionClick(Player player) {
        player.getInterfaceManager().close();
        fourthOption(player);
    }

}
