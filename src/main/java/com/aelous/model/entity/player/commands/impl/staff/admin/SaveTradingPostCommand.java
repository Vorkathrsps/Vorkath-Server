package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class SaveTradingPostCommand implements Command {
    @Override
    public void execute(Player player, String command, String[] parts) {

        try {
            TradingPost.save();
            TradingPost.saveRecentSales();
            player.getPacketSender().sendMessage("Saving tradepost listings..");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
