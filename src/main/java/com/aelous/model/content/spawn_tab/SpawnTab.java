package com.aelous.model.content.spawn_tab;

import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;

/**
 * @author Patrick van Elderen | May, 29, 2021, 03:14
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class SpawnTab {

    public static void spawn(Player player, int item, int amount, boolean toBank) {
        if(player.getIronManStatus() != IronMode.NONE) {
            player.message(Color.RED.wrap("As an ironman you stand alone."));
            return;
        }

        ItemDefinition def = World.getWorld().definitions().get(ItemDefinition.class, item);
        if(!player.canSpawn()) {
            return;
        }
        //Safety checks
        if (amount <= 0) {
            amount = 1;
        } else if (amount > Integer.MAX_VALUE) {
            amount = Integer.MAX_VALUE;
        }
        if(player.getInventory().getFreeSlots() == 0) {
            player.message("You can't spawn anymore items, your inventory is full.");
            return;
        }
        //Spawn item.
        if (toBank) {
            player.getBank().depositFromNothing(new Item(item, amount));
        } else {
            if (amount > player.getInventory().getFreeSlots()) {
                if (!def.stackable() && player.getInventory().contains(item)) {
                    amount = player.getInventory().getFreeSlots();
                }
            }
            player.getInventory().add(item, amount);
        }

        player.message("X "+amount+" "+def.name+" has been added in your "+ (toBank ? ("bank") : ("inventory")) +".");
    }

}
