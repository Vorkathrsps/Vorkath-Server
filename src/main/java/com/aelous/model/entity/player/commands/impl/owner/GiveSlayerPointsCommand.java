package com.aelous.model.entity.player.commands.impl.owner;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.QuestTab;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.entity.player.commands.impl.staff.admin.GiveItemCommand;
import com.aelous.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

import static com.aelous.model.entity.player.QuestTab.InfoTab.SLAYER_POINTS;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 11, 2020
 */
public class GiveSlayerPointsCommand implements Command {

    private static final Logger logger = LogManager.getLogger(GiveItemCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 3) {
            player.message("Invalid use of command.");
            player.message("Use: ::giveslayerpoints username amount");
            player.message("Example: ::giveitem t_e_s_t_e_r 1");
            return;
        }
        final String player2 = Utils.formatText(parts[1].replace("_", " "));
        int amount = Integer.parseInt(parts[2]);
        Optional<Player> plr = World.getWorld().getPlayerByName(player2);
        if (plr.isPresent()) {
            int currentPoints = plr.get().getAttribOr(AttributeKey.SLAYER_REWARD_POINTS, 0);
            int newPoints = currentPoints + amount;
            plr.get().putAttrib(AttributeKey.SLAYER_REWARD_POINTS, newPoints);
            plr.get().getPacketSender().sendString(SLAYER_POINTS.childId, QuestTab.InfoTab.INFO_TAB.get(SLAYER_POINTS.childId).fetchLineData(plr.get()));
            plr.get().message(player.getPlayerRights().getName() + " " + player.getUsername() + " has given you: " + amount+" slayer points.");
            logger.info(player.getPlayerRights().getName() + " " + player.getUsername() + " has given "+ player2 + ": " + amount+" slayer points.");
            player.message("You have given " + player2 + ": " + amount+" slayer points.");
        } else {
            player.message("The player " + player2 + " is not online.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isOwner(player));
    }

}
