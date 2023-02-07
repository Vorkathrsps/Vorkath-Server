package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author PVE
 * @Since september 13, 2020
 */
public class AlwaysHitCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length != 2) {
            player.message("Invalid use of command.");
            player.message("Usage: ::alwayshit value");
            return;
        }
        int hit = Integer.parseInt(parts[1]);
        if (hit > 0) {
            player.putAttrib(AttributeKey.ALWAYS_HIT, hit);
            player.message("You will always hit " + hit + " damage.");
        } else {
            player.clearAttrib(AttributeKey.ALWAYS_HIT);
            player.message("You will no longer hit constant damage.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }
}
