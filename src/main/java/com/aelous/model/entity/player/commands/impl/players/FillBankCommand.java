package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.GameServer;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.items.Item;
import com.aelous.model.items.container.ItemContainer;

import java.util.ArrayList;
import java.util.List;

import static com.aelous.GameConstants.BANK_ITEMS;
import static com.aelous.GameConstants.TAB_AMOUNT;

/**
 * @author PVE
 * @Since september 13, 2020
 */
public class FillBankCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (!GameServer.properties().pvpMode && !player.getPlayerRights().isDeveloper(player)) {
            return;
        }
        //if (player.ironMode() != IronMode.NONE) {
        //    player.message("As an ironman you cannot use this command.");
        //    return;
       // }
        player.getBank().addAll(BANK_ITEMS);
        System.arraycopy(TAB_AMOUNT, 0, player.getBank().tabAmounts, 0, TAB_AMOUNT.length);
        player.getBank().shift();
        System.out.println(BANK_ITEMS.length);
        player.message("Check your bank for your recieved items!");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
