package com.cryptic.model.entity.player.commands.impl.member;

import com.cryptic.GameServer;
import com.cryptic.model.entity.combat.CombatFactory;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.entity.player.rights.PlayerRights;
import com.cryptic.model.map.position.areas.impl.WildernessArea;
import com.cryptic.utility.Color;

public class BankCommandCommand implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        if(!player.getPlayerRights().isCommunityManager(player) && WildernessArea.isInWilderness(player)) {
            player.message(STR."<col=\{Color.RED.getColorValue()}>You can't use this command here.");
            return;
        }

        if(player.getRaids() != null && player.getRaids().raiding(player)) {
            player.message("You cannot use this command whilst raiding.");
            return;
        }

        if (!player.getPlayerRights().isCommunityManager(player) && CombatFactory.inCombat(player)) {
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
        return player.getPlayerRights().isAdministrator(player);
    }

}
