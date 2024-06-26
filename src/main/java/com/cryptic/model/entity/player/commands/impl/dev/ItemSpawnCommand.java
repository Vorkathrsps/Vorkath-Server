package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;
import org.apache.commons.lang3.StringUtils;

public class ItemSpawnCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (player.getIronManStatus() != IronMode.NONE && !player.getPlayerRights().isAdministrator(player)) {
            player.message("As an ironman you cannot use this command.");
            return;
        }

        int amount = 1;
        if (parts.length < 1 || (!StringUtils.isNumeric(parts[1]) || (parts.length > 2 && !StringUtils.isNumeric(parts[2])))) {
            player.message("Invalid syntax. Please use: ::item [ID] (amount)");
            player.message("Example: ::item 385 or ::item 385 20");
            return;
        }
        if (parts.length > 2) {
            amount = Integer.parseInt(parts[2]);
        }

        int id = Integer.parseInt(parts[1]);

       Item item = new Item(id);

        /*if(item.getId() > 34_000) {
           player.message("Item id not supported, this item doesn't exist.");
            return;
        }*/

        if (!player.canSpawn() && !player.getPlayerRights().isAdministrator(player)) {
            player.message("You can't spawn items here.");
            return;
        }

        if (Item.valid(item)) {
            player.getInventory().add(new Item(id, amount));
            player.message("You have just spawned x"+amount+" "+new Item(Integer.parseInt(parts[1])).unnote().name()+".");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }
}
