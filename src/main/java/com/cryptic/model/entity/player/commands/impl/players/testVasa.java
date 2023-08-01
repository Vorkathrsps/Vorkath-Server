package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.raids.party.Party;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

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
