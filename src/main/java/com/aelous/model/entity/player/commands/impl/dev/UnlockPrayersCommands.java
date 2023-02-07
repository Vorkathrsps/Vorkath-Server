package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class UnlockPrayersCommands implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        int type = Integer.parseInt(parts[1]);
        if (type == 0) {
            player.putAttrib(AttributeKey.PRESERVE, true);
        } else if (type == 1) {
            player.putAttrib(AttributeKey.RIGOUR, true);
        } else if (type == 2) {
            player.putAttrib(AttributeKey.AUGURY, true);
        }
        boolean preserve_unlocked = player.getAttribOr(AttributeKey.PRESERVE, false);
        boolean rigour_unlocked = player.getAttribOr(AttributeKey.RIGOUR, false);
        boolean augury_unlocked = player.getAttribOr(AttributeKey.AUGURY, false);
        player.getPacketSender().sendConfig(708, preserve_unlocked ? 1 : 0);
        player.getPacketSender().sendConfig(710, rigour_unlocked ? 1 : 0);
        player.getPacketSender().sendConfig(712, augury_unlocked ? 1 : 0);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
