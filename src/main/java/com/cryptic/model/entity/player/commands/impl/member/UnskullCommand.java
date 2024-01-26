package com.cryptic.model.entity.player.commands.impl.member;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.combat.skull.Skulling;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.areas.impl.WildernessArea;

public class UnskullCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        boolean member = player.getMemberRights().isRegularMemberOrGreater(player);
        if(!member && !player.getPlayerRights().isCommunityManager(player)) {
            player.message("You need to be at least a regular member to use this command.");
            return;
        }
        if (CombatFactory.inCombat(player) || WildernessArea.isInWilderness(player)) {
            player.message("You can't do that right now.");
            return;
        }
        player.message("You have used the unskull command.");
        Skulling.unskull(player);
        player.clearAttrib(AttributeKey.PVP_WILDY_AGGRESSION_TRACKER);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
