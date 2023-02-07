package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.aelous.model.entity.player.QuestTab.InfoTab.TASK_STREAK;


public class SetSlayerStreakCommand implements Command {

    private static final Logger logger = LogManager.getLogger(GiveItemCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 3) {
            player.message("Invalid use of command.");
            player.message("Use: ::setslayerstreak username amount");
            player.message("Example: ::setslayerstreak t_e_s_t 1");
            return;
        }
        final String player2 = Utils.formatText(parts[1].replace("_", " "));
        int amount = Integer.parseInt(parts[2]);
        Optional<Player> plr = World.getWorld().getPlayerByName(player2);
        if (plr.isPresent()) {
            if(plr.get().getUsername().equalsIgnoreCase(player.getUsername()) && !player.getPlayerRights().isAdministrator(player)) {
                player.message("You can't set the slayer streak for yourself.");
                return;
            }
            plr.get().putAttrib(AttributeKey.SLAYER_TASK_SPREE, amount);
            plr.get().message(player.getPlayerRights().getName() + " " + player.getUsername() + " has set your slayer task streak to: " + amount + ".");
            logger.info(player.getPlayerRights().getName() + " " + player.getUsername() + " has set the slayer task streak of "+ player2 + " to: " + amount + ".");
            player.message("You have set the slayer task streak of " + player2 + " to: " + amount + ".");
            player.getPacketSender().sendString(TASK_STREAK.childId, QuestTab.InfoTab.INFO_TAB.get(TASK_STREAK.childId).fetchLineData(player));
        } else {
            player.message("The player " + player2 + " is not online.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
