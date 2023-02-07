package com.aelous.model.entity.player.commands.impl.member;

import com.aelous.GameServer;
import com.aelous.model.entity.combat.CombatFactory;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;
import com.aelous.model.map.position.areas.impl.WildernessArea;
import com.aelous.utility.Color;

public class BankCommandCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(!player.getMemberRights().isLegendaryMemberOrGreater(player) && (!player.getPlayerRights().isDeveloper(player) && !GameServer.properties().test)) {
            player.message("You need to be at least a Dragonstone Member to use this command.");
            return;
        }

        if(!player.getPlayerRights().isDeveloper(player) && WildernessArea.inWild(player)) {
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
