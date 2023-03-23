package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.combat.prayer.default_prayer.Prayers;
import com.aelous.model.entity.masks.Flag;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.Command;

public class MasterCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
            player.getSkills().setXp(skill, Skills.levelToXp(99));
            player.getSkills().update();
            player.getSkills().recalculateCombat();
        }
        // Turn off prayers
        Prayers.closeAllPrayers(player);
        player.getUpdateFlag().flag(Flag.APPEARANCE);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
