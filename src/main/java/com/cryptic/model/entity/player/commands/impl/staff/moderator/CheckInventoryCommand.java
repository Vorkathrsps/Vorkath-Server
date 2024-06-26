package com.cryptic.model.entity.player.commands.impl.staff.moderator;

import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class CheckInventoryCommand implements Command {

    private static final Logger logger = LogManager.getLogger(CheckInventoryCommand.class);
    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setNameScript("Lookup Player", value -> {
            String name = (String) value;
            final String player2 = Utils.formatText(name);
            Optional<Player> plr = World.getWorld().getPlayerByName(player2);
            if (plr.isPresent()) {
                sendInventory(player, plr.get());
                player.message("The bank for " + player2 + " has been send to the VPS logs, ask Patrick or Supra for the log.");
                logger.info("Bank for {} is: {}", player2, plr.get().getBank().toString());
                return true;
            }
            player.message(name + " does not exist in our database.");
            return false;
        });
    }

    private void sendInventory(Player player, Player plr) {
        player.getPacketSender().sendInterface(27200);
        player.getPacketSender().sendString(27202, plr.getUsername() + "'s Inventory");
        for (int index = 0; index < 500; index++) {
            player.getPacketSender().sendItemOnInterfaceSlot(27201, null, index);
        }
        for (int index = 0; index < plr.inventory().size(); index++) {
            var item = plr.inventory().get(index);
            player.getPacketSender().sendItemOnInterfaceSlot(27201, item, index);
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isModerator(player));
    }

}
