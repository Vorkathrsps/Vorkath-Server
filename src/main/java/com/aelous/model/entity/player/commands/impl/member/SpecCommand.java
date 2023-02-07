package com.aelous.model.entity.player.commands.impl.member;

import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.combat.CombatSpecial;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.Tile;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;

/**
 * @author Patrick van Elderen <patrick.vanelderen@live.nl>
 * mei 21, 2020
 */
public class SpecCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(!player.getPlayerRights().isAdministrator(player) && ((!player.tile().inArea(Tile.EDGEVILE_HOME_AREA) || WildernessArea.inWild(player)))) {
            player.message("<col="+ Color.RED.getColorValue()+">You can only restore your special attack at home.");
            return;
        }

        if (!player.getPlayerRights().isAdministrator(player) && CombatFactory.inCombat(player)) {
            player.message("You cannot use this command during combat.");
            return;
        }

        boolean legendaryMember = player.getMemberRights().isLegendaryMemberOrGreater(player);
        if(!legendaryMember && !player.getPlayerRights().isAdministrator(player)) {
            player.message("You need to be at least a Dragonstone Member to use this command.");
            return;
        }

        int amt = 100;
        if (parts.length > 1 && player.getPlayerRights().isAdministrator(player)) {
            amt = Integer.parseInt(parts[1]);
        }
        player.setSpecialAttackPercentage(amt);
        player.setSpecialActivated(false);
        CombatSpecial.updateBar(player);
        player.message("<col="+ Color.HOTPINK.getColorValue()+">Special energy has been restored to full.");
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
