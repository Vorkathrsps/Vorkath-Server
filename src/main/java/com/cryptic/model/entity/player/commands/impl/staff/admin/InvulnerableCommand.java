package com.cryptic.model.entity.player.commands.impl.staff.admin;

import com.cryptic.core.task.TaskManager;
import com.cryptic.core.task.impl.ForceMovementTask;
import com.cryptic.model.entity.masks.ForceMovement;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.Tile;

public class InvulnerableCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length > 1) {
            player.setInvulnerable(parts[1].equalsIgnoreCase("true") || parts[1].equalsIgnoreCase("on"));
        } else {
            player.setInvulnerable(!player.isInvulnerable());
        }
        player.message("Infinite HP is now " + (player.isInvulnerable() ? "enabled" : "disabled") + ".");
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }

    /**
     * @author Ynneh | 29/03/2022 - 07:45
     * <https://github.com/drhenny>
     */
    public static class ForcemoveCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            player.getPacketSender().sendMessage("Force moving..");
            try {
                TaskManager.submit(new ForceMovementTask(player, new ForceMovement(player.tile().clone(), new Tile(0, 5), 30, 10, 0)));
            } catch (Exception e) {
                e.printStackTrace();
            }
                //Tile start, Tile end, int speed, int reverseSpeed, int direction
        }

        @Override
        public boolean canUse(Player player) {
            return true;
        }
    }
}
