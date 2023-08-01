package com.cryptic.model.content.packet_actions.interactions.equipment;

import com.cryptic.model.content.items.teleport.ArdyCape;
import com.cryptic.model.content.skill.impl.slayer.content.SlayerRing;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteractionManager;

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
