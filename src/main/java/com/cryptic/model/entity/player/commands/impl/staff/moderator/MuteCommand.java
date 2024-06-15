package com.cryptic.model.entity.player.commands.impl.staff.moderator;

import com.cryptic.GameEngine;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Color;
import com.cryptic.utility.PlayerPunishment;
import com.cryptic.utility.Utils;

public class MuteCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        player.setNameScript("Mute Player", value -> {
            final String name = (String) value;
            final String input = Utils.formatText(name);
            GameEngine.getInstance().submitLowPriority(() -> {
                if (player.getUsername().equalsIgnoreCase(input)) {
                    player.message(Color.MITHRIL.wrap("<img=13> You cannot mute yourself.</img>"));
                    return;
                }

                if (PlayerPunishment.muted(input)) {
                    player.message(Color.MITHRIL.wrap("<img=13> Player " + input + " is already muted.</img>"));
                    return;
                }

                GameEngine.getInstance().addSyncTask(() -> {
                    PlayerPunishment.mute(input);
                    player.message(Color.MITHRIL.wrap("<img=13> Player " + input + " has been successfully muted.</img>"));
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
