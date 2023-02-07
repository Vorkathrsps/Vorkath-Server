package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.items.tradingpost.PlayerListing;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.items.Item;

import static com.aelous.utility.ItemIdentifiers.*;

/**
 * @author Patrick van Elderen | May, 12, 2021, 14:04
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class SaveTPCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        TradingPost.listSale(player, new Item(ABYSSAL_WHIP, 5), 1_000_000);
        TradingPost.listSale(player, new Item(ARMADYL_CROSSBOW, 1), 10_000_000);
        TradingPost.listSale(player, new Item(ARMADYL_GODSWORD, 1), 112_000_000);
        TradingPost.listSale(player, new Item(DRAGON_CLAWS, 1), 211_000_000);
        TradingPost.listSale(player, new Item(SCYTHE_OF_VITUR, 1), 343_000_000);
        PlayerListing listing = TradingPost.sales.getOrDefault(player.getUsername().toLowerCase(), TradingPost.getListings(player.getUsername()));

        if (listing != null) {
            TradingPost.save(listing);
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
