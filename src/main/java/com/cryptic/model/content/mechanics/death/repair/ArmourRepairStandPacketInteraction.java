package com.cryptic.model.content.mechanics.death.repair;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.object.GameObject;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;

public class ArmourRepairStandPacketInteraction extends PacketInteraction {
    @Override
    public boolean handleItemOnObject(Player player, Item item, GameObject obj) {
        if (obj.getId() == 6802) {
            for (var i : Breakable.values()) {
                if (i.getRepairCost() != -1) {
                    if (i.brokenId == item.getId()) {
                        if (!player.getInventory().contains(995, i.getRepairCost())) {
                            player.message(Color.RED.wrap("You do not have enough gold to repair your " + item.name() + "."));
                            player.message(Color.PURPLE.wrap("The cost to repair " + item.name() + " is " + i.getFormattedRepairCost() + " coins."));
                            return true;
                        }
                        player.getInventory().remove(995, i.getRepairCost());
                        player.getInventory().remove(i.brokenId);
                        player.getInventory().add(new Item(i.id));
                        player.message(Color.BLUE.wrap("You repaired your " + item.name() + "."));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
