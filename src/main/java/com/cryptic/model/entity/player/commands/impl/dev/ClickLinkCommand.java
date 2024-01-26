package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Utils;

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
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
