package com.cryptic.model.content.skill.impl.thieving;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;

public class CoinPouch extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (item == null) return false;
        if (player.getInventory().contains(22522) && item.getId() == 22522) {
            addCoins(player, item, 25, 50);
            return true;
        } else if (player.getInventory().contains(22523) && item.getId() == 22523) {
            addCoins(player, item, 50, 100);
            return true;
        } else if (player.getInventory().contains(22524) && item.getId() == 22524) {
            addCoins(player, item, 100, 150);
            return true;
        } else if (player.getInventory().contains(22525) && item.getId() == 22525) {
            addCoins(player, item, 200, 250);
            return true;
        } else if (player.getInventory().contains(22526) && item.getId() == 22526) {
            addCoins(player, item, 300, 350);
            return true;
        } else if (player.getInventory().contains(22527) && item.getId() == 22527) {
            addCoins(player, item, 400, 450);
            return true;
        } else if (player.getInventory().contains(22528) && item.getId() == 22528) {
            addCoins(player, item, 500, 550);
            return true;
        } else if (player.getInventory().contains(22529) && item.getId() == 22529) {
            addCoins(player, item, 600, 650);
            return true;
        } else if (player.getInventory().contains(22530) && item.getId() == 22530) {
            addCoins(player, item, 700, 750);
            return true;
        }
        return false;
    }

    private void addCoins(Player player, Item item, int min, int max) {
        for (int index = 0; index < item.getAmount(); index++) {
            player.getInventory().remove(item);
            player.getInventory().add(new Item(995, random(min, max)));
        }
    }

    public int random(int min, int max) {
        return World.getWorld().random(min, max) * 10;
    }
}
