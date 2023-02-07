package com.aelous.model.entity.player.commands.impl.staff.moderator;

import com.aelous.model.World;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.PlayerStatus;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.areas.impl.WildernessArea;

import java.util.Optional;

public class TeleToMePlayerCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (command.length() <= 9)
            return;
        Optional<Player> plr = World.getWorld().getPlayerByName(command.substring(parts[0].length() + 1));
        if (plr.isPresent()) {
            if(plr.get().getStatus() == PlayerStatus.TRADING && !player.getPlayerRights().isDeveloper(player)) {
                player.message(plr.get().getUsername()+" is in a active trade, you cannot teleport to "+plr.get().getUsername()+".");
                return;
            }
            if(plr.get().getStatus() == PlayerStatus.DUELING && !player.getPlayerRights().isDeveloper(player)) {
                player.message(plr.get().getUsername()+" is in a active duel, you cannot teleport to "+plr.get().getUsername()+".");
                return;
            }
            if(plr.get().getStatus() == PlayerStatus.GAMBLING && !player.getPlayerRights().isDeveloper(player)) {
                player.message(plr.get().getUsername()+" is in a active gamble, you cannot teleport to "+plr.get().getUsername()+".");
                return;
            }
            if(WildernessArea.inWilderness(plr.get().tile()) && CombatFactory.inCombat(plr.get()) && !player.getPlayerRights().isDeveloper(player)) {
                player.message(plr.get().getUsername()+" is in the wilderness and in combat, you cannot teleport to "+plr.get().getUsername()+".");
                return;
            }
            plr.get().teleport(player.tile().clone());
        }
    }

    @Override
    public boolean canUse(Player player) {
        return (player.getPlayerRights().isAdministrator(player));
    }

}
