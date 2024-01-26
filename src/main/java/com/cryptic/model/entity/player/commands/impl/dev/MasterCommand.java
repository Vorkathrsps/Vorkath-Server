package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.entity.combat.prayer.default_prayer.Prayers;
import com.cryptic.model.entity.masks.Flag;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.entity.player.commands.Command;

public class MasterCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
            player.getSkills().setXp(skill, 200_000_000);
            player.getSkills().update();
            player.getSkills().recalculateCombat();
        }
        // Turn off prayers
        Prayers.closeAllPrayers(player);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isCommunityManager(player));
    }

}
