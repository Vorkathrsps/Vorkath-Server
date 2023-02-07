package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;

public class ClickLinkCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        String msg = "";
        String link = parts[1];
        for (int i = 2; i < parts.length; i++) {
            msg += (i == 2 ? "" : " ") + parts[i];
        }
        player.message("<link=" + link + ">" + " " + Utils.capitalizeFirst(msg));
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
