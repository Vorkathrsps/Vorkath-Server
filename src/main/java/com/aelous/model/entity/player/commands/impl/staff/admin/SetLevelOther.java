package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.World;
import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;

import java.util.Optional;

public class SetLevelOther implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 4) {
            player.message("Invalid use of command.");
            player.message("Example: ::setlevelo hc_skii skill_id lvl");
            return;
        }
        String username = Utils.formatText(parts[1].replace("_", " ")); // after "setlevelo "
        try {
            int skill_id = Integer.parseInt(parts[2]);
            int lvl = Integer.parseInt(parts[3]);
            Optional<Player> plr = World.getWorld().getPlayerByName(username);
            if (plr.isPresent()) {
                if (player.getHostAddress().equalsIgnoreCase(plr.get().getHostAddress()) &&
                    !player.getPlayerRights().isDeveloper(player)) {
                    player.message("You can't set levels for yourself or for players on the same IP.");
                    return;
                }

                if (!(skill_id >= 0 && skill_id < Skills.SKILL_COUNT)) {
                    player.message("Invalid skill id: " + skill_id);
                    return;
                }

                if (skill_id == Skills.HITPOINTS && lvl < 10) {
                    player.message("Hitpoints cannot go under <col=FF0000>10</col>.");
                    lvl = 10;
                }

                // Turn off prayers
                Prayers.closeAllPrayers(plr.get());

                plr.get().getSkills().setXp(skill_id, Skills.levelToXp(Math.min(99, lvl)));
                plr.get().getSkills().update();
                plr.get().getSkills().recalculateCombat();
                player.message("<col=FF0000>" + plr.get().getUsername()+ "'s " + Skills.SKILL_NAMES[skill_id] + "</col> set to <col=FF0000>" + plr.get().getSkills().levels()[skill_id] + "</col>.");
            } else {
                player.message("The player " + username + " is not online.");
            }
        } catch (NumberFormatException e) {
            player.message("Command failed: incorrect usage of the command.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }

    /**
     * @author Ynneh | 01/04/2022 - 08:53
     * <https://github.com/drhenny>
     */
    public static class CtrlTeleportCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            Integer x = Integer.valueOf(parts[1]);
            Integer y = Integer.valueOf(parts[2]);
            Integer z = Integer.valueOf(parts[3]);
            player.teleport(x, y, player.tile().getZ()); // fuck what the client thinks about height
        }

        @Override
        public boolean canUse(Player player) {
            return player.getPlayerRights().isAdministrator(player);
        }
    }

    /**
     * @author Ynneh | 02/03/2022 - 00:15
     * <https://github.com/drhenny>
     */
    public static class TestProjecttile implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            int delay = 51;
            int delayMod = -60;// because server is old-fashion has manual server 2 tick delay
            //new Projectile(player.tile(), new Tile(player.getX(), player.getY()+2), -1, 1465, 12 * 5, 10, 43, 35, 0, 16, 64).sendProjectile();
        }

        /**
         *  public PacketSender sendProjectile(Tile tile, Tile offset, int angle, int speed, int id, int startHeight, int endHeight, int lockon, int delay, int slope, int radius) {
         *         sendPosition(tile);
         *         PacketBuilder out = new PacketBuilder(117);
         *         out.put(angle);
         *         out.put(offset.getY());
         *         out.put(offset.getX());
         *         out.putShort(lockon);
         *         out.putShort(id);
         *         out.put(startHeight);
         *         out.put(endHeight);
         *         out.putShort(delay);
         *         out.putShort(speed);
         *         out.put(slope);
         *         out.put(radius);
         *         player.getSession().write(out);
         *         //System.out.println(String.format("pos %s offset %s angle %d speed %d id %d startHeight %d endHeight %d lockon %d delay %d slope %d radius %d%n", position, offset, angle, speed, id, startHeight, endHeight, lockon, delay, slope, radius));
         *         return this;
         *     }
         * @param player
         * @return
         */

        @Override
        public boolean canUse(Player player) {
            return false;
        }

    }
}
