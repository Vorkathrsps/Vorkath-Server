package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.items.tradingpost.TradingPost;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | May, 12, 2021, 14:04
 * 
 */
public class SaveAllTPCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        TradingPost.save();
        player.requestLogout();
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isDeveloper(player);
    }
}
