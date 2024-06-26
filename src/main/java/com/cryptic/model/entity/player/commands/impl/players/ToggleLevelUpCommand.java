package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | January, 11, 2021, 18:33
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class ToggleLevelUpCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        var active = player.<Boolean>getAttribOr(AttributeKey.LEVEL_UP_INTERFACE, false);

        active = !active;
        player.putAttrib(AttributeKey.LEVEL_UP_INTERFACE, active);
        player.message("Your level up messages have been " + (active ? "enabled." : "disabled."));
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
