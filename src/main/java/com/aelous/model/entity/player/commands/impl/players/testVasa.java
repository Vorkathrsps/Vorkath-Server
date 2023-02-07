package com.aelous.model.entity.player.commands.impl.players;

import com.aelous.model.content.raids.party.Party;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.commands.Command;

public class testVasa implements Command {

    @Override
    public void execute(Player player, String command, String[] parts) {
        Party party = player.raidsParty;
        party.setRaidStage(2);
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
