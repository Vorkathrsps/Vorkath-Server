package com.cryptic.model.entity.player.commands.impl.dev;

import com.cryptic.model.content.mechanics.Poison;
import com.cryptic.model.World;
import com.cryptic.model.entity.combat.Venom;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.Skills;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.utility.Utils;

import java.util.Optional;

/**
 * @author Origin | June, 12, 2021, 14:25
 * 
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
        return (player.getPlayerRights().isCommunityManager(player));
    }
}
