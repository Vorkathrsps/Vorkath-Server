package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.Command;

public class ResetCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        for (int skill = 0; skill < Skills.SKILL_COUNT; skill++) {
            int level = skill == Skills.HITPOINTS ? 10 : 1;
            player.getSkills().setLevel(skill, level);
            player.getSkills().setXp(skill, Skills.levelToXp(level));
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isDeveloper(player));
    }

}
