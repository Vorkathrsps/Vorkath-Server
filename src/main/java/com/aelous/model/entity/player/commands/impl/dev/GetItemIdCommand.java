package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.GameServer;
import com.aelous.cache.definitions.ItemDefinition;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.items.Item;
import com.aelous.utility.Color;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * This command is used to send a list of items and their id's to the player based on the search term.
 * @author Bananastreet
 */
public class
GetItemIdCommand implements Command {

    private static final boolean ALLOW_SPAWN = true;

    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 2) {
            player.message("Invalid syntax. Please use: ::getid [name]");
            player.message("Example: ::getid claws");
            return;
        }
        String itemName = command.substring(parts[0].length() + 1);
        if (itemName.length() < 3) {
            player.message("You must give at least 3 letters of input to narrow down the item.");
            return;
        }
        int results = 0;
        player.message("Searching: " + itemName);// used to be ("Searching: " + input)
        for (int j = 0; j < World.getWorld().definitions().total(ItemDefinition.class); j++) {
            if (results >= 75) {
                player.message("Over 75 results have been found, the maximum number of allowed results. If you cannot");
                player.message("find the item, try and enter more characters to refine the results.");
                return;
            }
            Item item = new Item(j);
            if (item.name() != null && !item.name().equalsIgnoreCase("null")) {
                if (item.name().replace("_", " ").toLowerCase().contains(itemName)) {
                        player.message("<col=" + Color.MEDRED.getColorValue() + ">" + item.name().replace("_", " ") + " - " + item.getId());
                        results++;
                    }
                }
            }
        player.message(results + " results found...");
    }

    @Override
    public boolean canUse(Player player) {
        if(ALLOW_SPAWN) {
            return true;
        }
        if(GameServer.properties().test) {
            return true;
        }
        return (player.getPlayerRights().isAdministrator(player));
    }
}
