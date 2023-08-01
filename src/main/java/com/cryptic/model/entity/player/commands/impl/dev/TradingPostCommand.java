package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Patrick van Elderen | April, 14, 2021, 13:24
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class TradingPostCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        TradingPost.open(player);
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
