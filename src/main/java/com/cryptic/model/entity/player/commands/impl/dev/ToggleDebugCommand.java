package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class ToggleDebugCommand implements Command {


    public void execute(Player player, String command, String[] parts) {
        boolean debugMessages = player.getAttribOr(AttributeKey.DEBUG_MESSAGES, true);
        debugMessages = !debugMessages;
        player.putAttrib(AttributeKey.DEBUG_MESSAGES, debugMessages);
        player.message("Your debug messages are now " + (debugMessages ? "enabled." : "disabled."));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
