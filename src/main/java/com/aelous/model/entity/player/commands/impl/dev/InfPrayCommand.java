package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author PVE
 * @Since september 13, 2020
 */
public class InfPrayCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length > 1) {
            boolean active = parts[1].equalsIgnoreCase ("true") || parts[1].equalsIgnoreCase("on");
            player.putAttrib(AttributeKey.INF_PRAY, active);
        } else {
            player.putAttrib(AttributeKey.INF_PRAY, player.getAttribOr(AttributeKey.INF_PRAY, false));
        }
        player.message("infpray: " + (player.getAttribOr(AttributeKey.INF_PRAY, false) ? "enabled" : "disabled") + ".");
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }
}
