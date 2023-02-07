package com.aelous.model.content.packet_actions.interactions.equipment;

import com.aelous.model.content.items.teleport.ArdyCape;
import com.aelous.model.content.skill.impl.slayer.content.SlayerRing;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;

public class EquipmentActions {

    public static boolean operate(Player player, int slot, Item item) {
        ArdyCape.onEquipmentOption(player, item, slot);

        if(PacketInteractionManager.onEquipmentAction(player, item, slot)) {
            return true;
        }

        if (SlayerRing.onEquipmentOption(player, item, slot)) {
            return true;
        }
        return false;
    }
}
