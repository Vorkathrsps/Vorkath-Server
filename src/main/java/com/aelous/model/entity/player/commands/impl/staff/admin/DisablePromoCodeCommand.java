package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.commands.impl.players.PromoCodeCommand;

/**
 * @author Patrick van Elderen <https://github.com/PVE95>
 * @Since October 30, 2021
 */
public class DisablePromoCodeCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        PromoCodeCommand.PROMO_CODE_COMMAND_ENABLED =! PromoCodeCommand.PROMO_CODE_COMMAND_ENABLED;
        String msg = PromoCodeCommand.PROMO_CODE_COMMAND_ENABLED ? "Disabled" : "Enabled";
        player.message("The promo code command is now "+msg+".");
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
