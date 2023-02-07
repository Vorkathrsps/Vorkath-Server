package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class ToggleDidYouKnowCommand implements Command {


    public void execute(Player player, String command, String[] parts) {
        boolean didYouKnow = player.getAttribOr(AttributeKey.DID_YOU_KNOW, true);
        didYouKnow = !didYouKnow;
        player.putAttrib(AttributeKey.DID_YOU_KNOW, didYouKnow);
        player.message("Your did you know messages are now " + (didYouKnow ? "enabled." : "disabled."));
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
