package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.master.impl.SlayerMasterDialogue;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

public class SlayerActionCommand implements Command {
    @Override
    public void execute(Player player, String command, String[] parts) {
        String action = parts[1];

        if (action.equalsIgnoreCase("reset")) {
            Slayer.cancelTask(player, true);
            player.message("Your slayer task has been reset, talk to any slayer master for a new task.");
        } else if (action.equalsIgnoreCase("task")) {
            player.getDialogueManager().start(new SlayerMasterDialogue());
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isCommunityManager(player);
    }
}
