package com.aelous.model.entity.player.commands.impl.member;

import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 21, 2020
 */
public class HealCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        boolean extremeMember = player.getMemberRights().isEliteMemberOrGreater(player);
        if(!extremeMember && !player.getPlayerRights().isAdministrator(player)) {
            player.message("You need to be at least a elite member to use this command.");
            return;
        }
        if(((!player.tile().inArea(Tile.EDGEVILE_HOME_AREA) || WildernessArea.inWild(player)))&& !player.getPlayerRights().isAdministrator(player)) {
            player.message("<col="+ Color.RED.getColorValue()+">You can only restore your health at home.");
            return;
        }
        if (CombatFactory.inCombat(player) && !player.getPlayerRights().isAdministrator(player)) {
            player.message("You cannot use this command during combat.");
            return;
        }
        player.heal();
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
