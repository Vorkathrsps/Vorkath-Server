package com.cryptic.model.entity.player.commands.impl.member;

import com.cryptic.model.World;
import com.cryptic.model.content.skill.impl.slayer.Slayer;
import com.cryptic.model.content.skill.impl.slayer.master.impl.SlayerMasterDialogue;
import com.cryptic.model.content.skill.impl.slayer.slayer_task.SlayerTask;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

/**
 * @author Origin | June, 10, 2021, 22:27
 * 
 */
public class NewTaskCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(!player.getMemberRights().isLegendaryMemberOrGreater(player)) {
            player.message("You have to be at least an Dragonstone Member to use this command.");
            return;
        }

        if(WildernessArea.isInWilderness(player)) {
            player.message("You can't use that command here.");
            return;
        }

        SlayerTask slayer = World.getWorld().getSlayerTasks();
        slayer.sendCancelTaskDialouge(player);
        player.getDialogueManager().start(new SlayerMasterDialogue());
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
