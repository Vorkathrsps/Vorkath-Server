package com.aelous.model.entity.player.commands.impl.staff.admin;

import com.aelous.model.World;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;

import java.util.Optional;

/**
 * @author Patrick van Elderen | March, 16, 2021, 08:55
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class SetMaxSkillsCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (parts.length < 2) {
            player.message("Invalid syntax. Please enter a username.");
            player.message("::setmaxskills username");
            return;
        }
        String username = Utils.formatText(command.substring(12)); // after "setmaxskills "
        Optional<Player> plr = World.getWorld().getPlayerByName(username);
        if (plr.isPresent()) {
            //Take away one because construction is un trainable
            for (int skill = 0; skill < Skills.SKILL_COUNT - 1; skill++) {
                plr.get().getSkills().setXp(skill, Skills.levelToXp(99));
                plr.get().getSkills().update();
                plr.get().getSkills().recalculateCombat();
                plr.get().putAttrib(AttributeKey.COMBAT_MAXED,true);
            }

            player.message("You have maxed all trainable skills for "+plr.get().getUsername()+".");
            plr.get().message("Your account has been maxed out.");
        } else {
            player.message(username+" is currently not online or doesn't exist.");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
