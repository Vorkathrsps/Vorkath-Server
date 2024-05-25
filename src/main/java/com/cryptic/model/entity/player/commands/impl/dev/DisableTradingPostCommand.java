package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | July, 06, 2021, 02:30
 * 
 */
public class DisableTradingPostCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        TradingPost.TRADING_POST_ENABLED =! TradingPost.TRADING_POST_ENABLED;
        String msg = TradingPost.TRADING_POST_ENABLED ? "Enabled" : "Disabled";
        player.message("The trading post is "+msg+".");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isCommunityManager(player);
    }
}
