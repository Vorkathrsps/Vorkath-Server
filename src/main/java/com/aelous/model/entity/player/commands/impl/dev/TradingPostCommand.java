package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

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
