package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author Patrick van Elderen | July, 06, 2021, 02:30
 * @see <a href="https://github.com/PVE95">Github profile</a>
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
        return player.getPlayerRights().isDeveloper(player);
    }
}
