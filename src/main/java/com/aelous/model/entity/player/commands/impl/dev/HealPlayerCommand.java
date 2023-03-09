package com.aelous.model.entity.player.commands.impl.dev;

import com.aelous.model.content.mechanics.Poison;
import com.aelous.model.World;
import com.aelous.model.entity.combat.Venom;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.Skills;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.utility.Utils;

import java.util.Optional;

/**
 * @author Patrick van Elderen | June, 12, 2021, 14:25
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class HealPlayerCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        String username = Utils.formatText(command.substring(11)); // after "healplayer "

        Optional<Player> playerToHeal = World.getWorld().getPlayerByName(username);
        if (playerToHeal.isPresent()) {
            Player p = playerToHeal.get();
            player.message("You have healed "+p.getUsername()+".");
            p.hp(Math.max(player.getSkills().level(Skills.HITPOINTS), p.getSkills().xpLevel(Skills.HITPOINTS)), 20); //Set hitpoints to 100%
            p.getSkills().replenishSkill(5, p.getSkills().xpLevel(5)); //Set the players prayer level to full
            p.getSkills().replenishStatsToNorm();
            p.setRunningEnergy(100.0, true);
            Poison.cure(p);
            Venom.cure(2, p);
            p.message("You have been healed by "+player.getUsername()+".");
        }
    }

    @Override
    public boolean canUse(Player player) {
        return player.getPlayerRights().isAdministrator(player);
    }
}
