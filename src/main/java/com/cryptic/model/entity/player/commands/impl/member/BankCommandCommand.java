package com.cryptic.model.entity.player.commands.impl.member;

import com.cryptic.GameServer;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;

public class BankCommandCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(!player.getMemberRights().isLegendaryMemberOrGreater(player) && (!player.getPlayerRights().isDeveloper(player) && !GameServer.properties().test)) {
            player.message("You need to be at least a Dragonstone Member to use this command.");
            return;
        }

        if(!player.getPlayerRights().isDeveloper(player) && WildernessArea.isInWilderness(player)) {
            player.message("<col="+ Color.RED.getColorValue()+">You can't use this command here.");
            return;
        }

        if(player.getRaids() != null && player.getRaids().raiding(player)) {
            player.message("You cannot use this command whilst raiding.");
            return;
        }

        if (!player.getPlayerRights().isDeveloper(player) && CombatFactory.inCombat(player)) {
            player.message("You cannot use this command during combat.");
            return;
        }

        if(player.busy()) {
            player.message("You're to busy to use the ::bank command right now.");
            return;
        }

        player.getBank().open();
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

}
