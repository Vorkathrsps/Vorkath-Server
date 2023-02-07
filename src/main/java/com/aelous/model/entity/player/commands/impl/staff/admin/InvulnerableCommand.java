package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.core.task.TaskManager;
import com.aelous.core.task.impl.ForceMovementTask;
import com.aelous.model.entity.masks.ForceMovement;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;

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
        return (player.getPlayerRights().isDeveloper(player));
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
