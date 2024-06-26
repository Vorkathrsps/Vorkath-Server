package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | June, 21, 2021, 14:33
 * 
 */
public class VexiaCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.getPacketSender().sendURL("https://www.youtube.com/channel/UCgJRM-9eZU4NRsIr84r9BZg");
        player.message("Opening Vexia's channel in your web browser...");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    /**
     * @author Ynneh | 31/03/2022 - 15:33
     * <https://github.com/drhenny>
     */
    public static class XPLockCommand implements Command {

        @Override
        public void execute(Player player, String command, String[] parts) {
            boolean isLocked = player.getAttribOr(AttributeKey.XP_LOCKED, false);
            player.putAttrib(AttributeKey.XP_LOCKED, !isLocked);
            player.getPacketSender().sendMessage("You have "+(player.getAttribOr(AttributeKey.XP_LOCKED, false) ? "UNLOCKED" : "LOCKED")+" your xp.");
        }

        @Override
        public boolean canUse(Player player) {
            return true;
        }
    }
}
