package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author Patrick van Elderen | June, 21, 2021, 14:33
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class WalkchaosCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://www.youtube.com/user/Walkchaos3");
        player.message("Opening Walkchaos's channel in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    /**
     * @author Ynneh | 10/03/2022 - 04:40
     * <https://github.com/drhenny>
     */
    public static class ToggleCombatDebugCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            player.combatDebug = !player.combatDebug;
            player.getPacketSender().sendMessage("You have "+(player.combatDebug ? "<col=36ab19>ENABLED</col>" : "<col=ff0000>DISABLED</col>")+" your combat information.");
        }

        /***
         * 1_111 = barrage
         * @param player
         * @return
         */

        @Override
        public boolean canUse(Player player) {
            return true;
        }
    }

    /**
     * @author Ynneh | 08/03/2022 - 22:31
     * <https://github.com/drhenny>
     */
    public static class ClearRecentTeleportsCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            player.getPacketSender().resetRecentTeleports();
            player.getPacketSender().sendMessage("Cleared recent teleports..");
        }

        @Override
        public boolean canUse(Player player) {
            return true;
        }
    }

    /**
     * @author Ynneh | 09/03/2022 - 03:42
     * <https://github.com/drhenny>
     */
    public static class ClearFavoriteTeleportsCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            player.getPacketSender().sendMessage("Cleared "+player.getFavorites().size()+" favorites from your TP Favorites list.");
            player.getFavorites().clear();
            player.getPacketSender().clearFavorites();
        }

        @Override
        public boolean canUse(Player player) {
            return true;
        }
    }

    /**
     * @author Ynneh | 09/03/2022 - 03:40
     * <https://github.com/drhenny>
     */
    public static class CheckFavoriteTeleportsCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            System.err.println("Favorites Size: "+player.getFavorites().size());
            player.getFavorites().stream().filter(f -> f != null).forEach(f -> System.err.println(f.teleportName));
        }

        @Override
        public boolean canUse(Player player) {
            return true;
        }
    }
}
