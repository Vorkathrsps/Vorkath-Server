package com.aelous.model.content.minigames.impl.tempoross.objects;

import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.object.GameObject;
import com.aelous.network.packet.incoming.interaction.PacketInteraction;
import com.aelous.utility.ItemIdentifiers;

public class TemporossMinigameInteractions extends PacketInteraction {

    int buckets = 40966;
    int ropes = 40965;
    int water_pump_one = 41004;
    int water_pump_two = 41000;
    int hammers = 40964;
    int harpoons = 40967;
    int totem_pole = 41354;
    int cooking_shrine = 41236;

    @Override
    public boolean handleObjectInteraction(Player player, GameObject obj, int option) {
        if (obj.getId() == buckets) {
            if (option == 1) {
                if (!player.getInventory().isFull()) {
                    player.animate(536);
                    player.getInventory().add(new Item(1925, 1));
                    return true;
                } else {
                    player.message("You do not have enough space in your inventory.");
                    return false;
                }
            } else if (option == 2) {
                if (!player.getInventory().isFull() && player.getInventory().hasFreeSlots(5)) {
                    player.animate(536);
                    player.getInventory().add(new Item(1925, 5));
                    return true;
                } else {
                    player.message("You do not have enough space in your inventory.");
                    return false;
                }
            } else if (option == 3) {
                if (!player.getInventory().isFull() && player.getInventory().hasFreeSlots(10)) {
                    player.animate(536);
                    player.getInventory().add(new Item(1925, 10));
                    return true;
                } else {
                    player.message("You do not have enough space in your inventory.");
                    return false;
                }
            }
        } else if (obj.getId() == ropes) {
            if (!player.getInventory().isFull()) {
                player.animate(536);
                player.getInventory().add(new Item(954, 1));
                return true;
            } else {
                player.message("You do not have enough space in your inventory.");
                return false;
            }
        } else if (obj.getId() == hammers) {
            if (!player.getInventory().isFull()) {
                player.animate(536);
                player.getInventory().add(new Item(2347, 1));
                return true;
            } else {
                player.message("You do not have enough space in your inventory.");
                return false;
            }
        } else if (obj.getId() == harpoons) {
            if (!player.getInventory().isFull()) {
                player.animate(536);
                player.getInventory().add(new Item(311, 1));
                return true;
            } else {
                player.message("You do not have enough space in your inventory.");
                return false;
            }
        } else if (obj.getId() == water_pump_one || obj.getId() == water_pump_two) {
            if (player.getInventory().contains(ItemIdentifiers.BUCKET)) {
                player.animate(536);
                player.getInventory().remove(ItemIdentifiers.BUCKET);
                player.getInventory().add(new Item(ItemIdentifiers.BUCKET_OF_WATER));
                return true;
            } else {
                player.message("Your inventory does not contain a bucket.");
                return false;
            }
        }
        return false;
    }
}
