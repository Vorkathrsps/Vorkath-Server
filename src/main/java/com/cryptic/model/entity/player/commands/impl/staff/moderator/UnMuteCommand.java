package com.cryptic.model.entity.player.commands.impl.staff.moderator;

import com.cryptic.GameEngine;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Color;
import com.cryptic.utility.PlayerPunishment;
import com.cryptic.utility.Utils;

public class UnMuteCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setNameScript("UnMute Player", value -> {
            final String name = (String) value;
            final String input = Utils.formatText(name);
            GameEngine.getInstance().submitLowPriority(() -> {
                if (!PlayerPunishment.muted(input)) {
                    player.message(Color.MITHRIL.wrap("<img=13> Player " + input + " is not currently muted.</img>"));
                    return;
                }

                GameEngine.getInstance().addSyncTask(() -> {
                    PlayerPunishment.unmute(input);
                    player.message(Color.MITHRIL.wrap("<img=13> Player " + input + " has been successfully un-muted.</img>"));
                });
            });
            return true;
        });
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isModerator(player);
    }
}
