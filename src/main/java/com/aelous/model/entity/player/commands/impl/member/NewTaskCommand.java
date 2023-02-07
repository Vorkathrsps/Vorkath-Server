package com.aelous.model.entity.player.commands.impl.member;

import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.content.skill.impl.slayer.master.impl.SlayerMasterDialogue;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.areas.impl.WildernessArea;

/**
 * @author Patrick van Elderen | June, 10, 2021, 22:27
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class NewTaskCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(!player.getMemberRights().isLegendaryMemberOrGreater(player)) {
            player.message("You have to be at least an Dragonstone Member to use this command.");
            return;
        }

        if(WildernessArea.inWild(player)) {
            player.message("You can't use that command here.");
            return;
        }

        Slayer.cancelTask(player,true);
        player.getDialogueManager().start(new SlayerMasterDialogue());
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
