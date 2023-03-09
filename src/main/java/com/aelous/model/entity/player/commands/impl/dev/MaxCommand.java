package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.Command;

/**
 * @author PVE
 * @Since september 13, 2020
 */
public class MaxCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        // Boost stats instead of having to waste time potting when testing combat functions.
        player.getSkills().setLevel(Skills.ATTACK, 118);
        player.getSkills().setLevel(Skills.STRENGTH, 118);
        player.getSkills().setLevel(Skills.DEFENCE, 118);
        player.getSkills().setLevel(Skills.RANGED, 112);
        player.getSkills().setLevel(Skills.MAGIC, 104);
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }
}
