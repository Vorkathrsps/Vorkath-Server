package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.player.IronMode;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

import static com.cryptic.utility.ItemIdentifiers.COOKED_KARAMBWAN;

/**
 * @author Patrick van Elderen | June, 21, 2021, 14:30
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class ComboCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(player.getIronManStatus() != IronMode.NONE) {
            player.message("As an ironman you cannot use this command.");
            return;
        }

        if (!player.tile().inSafeZone() && !player.getPlayerRights().isAdministrator(player)) {
            player.message("You can only use this command at safe zones.");
            return;
        }

        if(WildernessArea.isInWilderness(player)) {
            player.message("You can only use this command at safe zones.");
            return;
        }

        if (player.inventory().hasCapacity(new Item(COOKED_KARAMBWAN))) {
            player.inventory().add(new Item(COOKED_KARAMBWAN,5));
        }
        player.message("You spawn some cooked karambwans.");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
