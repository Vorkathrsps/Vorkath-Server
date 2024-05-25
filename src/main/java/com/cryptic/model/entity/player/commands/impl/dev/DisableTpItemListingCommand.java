package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | May, 28, 2021, 15:57
 * 
 */
public class DisableTpItemListingCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        TradingPost.TRADING_POST_LISTING_ENABLED =! TradingPost.TRADING_POST_LISTING_ENABLED;
        String msg = TradingPost.TRADING_POST_LISTING_ENABLED ? "Enabled" : "Disabled";
        player.message("Trading post listing is now "+msg+".");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isCommunityManager(player);
    }
}
