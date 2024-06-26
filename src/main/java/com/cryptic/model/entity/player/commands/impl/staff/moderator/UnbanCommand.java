package com.cryptic.model.entity.player.commands.impl.staff.moderator;

import com.cryptic.GameEngine;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.entity.player.save.PlayerSave;
import com.cryptic.utility.Color;
import com.cryptic.utility.PlayerPunishment;
import com.cryptic.utility.Utils;

public class UnbanCommand implements Command {
    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setNameScript("UnBan Player", value -> {
            final String name = (String) value;
            final String input = Utils.formatText(name);
            GameEngine.getInstance().submitLowPriority(() -> {
                if (!PlayerSave.playerExists(input)) {
                    player.message(Color.MITHRIL.wrap("<img=13> Player " + input + " does not exist.</img>"));
                    return;
                }

                if (!PlayerPunishment.banned(input)) {
                    player.message(Color.MITHRIL.wrap("<img=13> Player " + input + " is not currently banned.</img>"));
                    return;
                }

                GameEngine.getInstance().addSyncTask(() -> PlayerPunishment.unban(input));
                player.message(Color.MITHRIL.wrap("<img=13> Player " + input + " was successfully unbanned.</img>"));
            });
            return true;
        });
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isModerator(player);
    }
}
