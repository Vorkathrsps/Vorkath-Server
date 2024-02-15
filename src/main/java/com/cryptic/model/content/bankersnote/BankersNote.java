package com.cryptic.model.content.bankersnote;

import com.cryptic.model.content.duel.Dueling;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.model.items.container.inventory.Inventory;
import com.cryptic.model.map.position.Tile;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.network.packet.incoming.interaction.PacketInteraction;
import com.cryptic.utility.Color;

public class BankersNote extends PacketInteraction {

    @Override
    public boolean handleItemInteraction(Player player, Item item, int option) {
        if (item.getId() == 28767) {
            if (WildernessArea.inWilderness(player.tile())) {
                player.message(Color.RED.wrap("You cannot use the " + item.name() + " inside of the wilderness."));
                return true;
            }
            if (Dueling.in_duel(player)) {
                player.message(Color.RED.wrap("You cannot use the " + item.name() + " inside of the duel arena."));
                return true;
            }
            player.getBank().open();
            return true;
        }
        return false;
    }
    @Override
    public boolean handleItemOnItemInteraction(Player player, Item use, Item usedWith) {
        int id = usedWith.getId();
        Tile tile = player.tile();
        boolean inWilderness = WildernessArea.inWilderness(tile);
        boolean isBankersNote = use.getId() == 28767;
        Inventory inventory = player.getInventory();
        if (isBankersNote && usedWith.noteable()) {
            if (inWilderness) {
                player.message(Color.RED.wrap("You cannot use the " + use.name() + " inside of the wilderness."));
                return true;
            }
            if (Dueling.in_duel(player)) {
                player.message(Color.RED.wrap("You cannot use the " + use.name() + " inside of the duel arena."));
                return true;
            }
            player.setAmountScript("How many would you like to note?", script -> {
                int amount = (int) script;
                if (amount <= 0) return false;
                if (inventory.count(id) < amount) {
                    amount = inventory.count(id);
                }
                for (int index = 0; index < amount; index++) {
                    /*if (!inventory.contains(usedWith.note().getId()) && inventory.isFull()) {
                        player.message("You do not have enough space in your inventory.");
                        break;
                    }*/
                    inventory.remove(id);
                    inventory.add(usedWith.note());
                }
                return true;
            });
            return true;
        } else {
            boolean isNoted = usedWith.noted();
            if (isBankersNote && isNoted) {
                if (inWilderness) {
                    player.message(Color.RED.wrap("You cannot use the " + use.name() + " inside of the wilderness."));
                    return true;
                }
                player.setAmountScript("How many would you like to un-note?", script -> {
                    int amount = (int) script;
                    if (amount <= 0) return false;
                    if (inventory.count(id) < amount) {
                        amount = inventory.count(id);
                    }
                    int unnoted = usedWith.unnote().getId();
                    for (int index = 0; index < amount; index++) {
                        if (inventory.count(id) > 1 && inventory.isFull()) {
                            player.message("You do not have enough space in your inventory.");
                            break;
                        }
                        inventory.remove(id);
                        inventory.add(unnoted);
                    }
                    return true;
                });
                return true;
            }
        }
        return false;
    }
}
