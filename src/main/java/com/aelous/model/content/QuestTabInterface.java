package com.aelous.model.content;

import com.aelous.GameConstants;
import com.aelous.model.entity.player.Player;
import com.aelous.network.packet.outgoing.PacketSender;
import com.aelous.utility.Color;
import com.google.common.collect.ImmutableSet;

public final class QuestTabInterface {

    private static final ImmutableSet<Integer> QUEST_LINES = ImmutableSet.of(8144, 8145, 8147, 8148, 8149, 8150,
        8151, 8152, 8153, 8154, 8155, 8156, 8157, 8158, 8159,
        8160, 8161, 8162, 8163, 8164, 8165, 8166, 8167, 8168,
        8169, 8170, 8171, 8172, 8173, 8174, 8175, 8176, 8177,
        8178, 8179, 8180, 8181, 8182, 8183, 8184, 8185, 8186,
        8187, 8188, 8189, 8190, 8191, 8192, 8193, 8194, 8195);

    public static void clearQuestInterface(final Player player) {
        final PacketSender sender = player.getPacketSender();
        for (int element : QUEST_LINES) {
            sender.sendString(element, "");
        }
    }

    public static void wildernessKeyInformation(Player player) {
        clearQuestInterface(player);
        player.getPacketSender().sendString(8144, "           "+Color.RED.tag()+"Wilderness key information:");
        player.getPacketSender().sendString(8147, "You can obtain a Wilderness chest key from the");
        //player.getPacketSender().sendString(8148, "Wilderess key event which happens every " + WildernessKeyPlugin.SPAWN_DURATION.toHours() + " hours.");
        player.getPacketSender().sendString(8149, "");
        player.getPacketSender().sendString(8150, "The key will spawn deep in the Wilderness, whoever");
        player.getPacketSender().sendString(8151, "survives to bring the key back to safety gets to loot");
        player.getPacketSender().sendString(8152, "the chest and get the treasure from inside it.");
        player.getInterfaceManager().open(8134);
    }

    public static void helpInterface(Player player) {
        clearQuestInterface(player);
        player.getPacketSender().sendString(8144, "                   "+Color.RED.tag()+"General Help:");
        player.getPacketSender().sendString(8147, "Thank you " + player.getUsername() + " for playing "+ GameConstants.SERVER_NAME+"!");
        player.getPacketSender().sendString(8149, "New player tips:");
        player.getPacketSender().sendString(8150, "1. Re-watch the tutorial by talking to the town crier.");
        player.getPacketSender().sendString(8151, "2. View available commands to you by using"+Color.RED.tag()+" ::Commands");
        player.getPacketSender().sendString(8152, "3. Read the "+Color.LIGHT_GREEN.tag()+"guide book"+Color.BLACK.tag()+" in your bank (loads of useful info!)");
        player.getPacketSender().sendString(8154, "Need more help? Talk to a staff member or use the forums.");
        player.getInterfaceManager().open(8134);
    }
}
