package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.services.database.Vote;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;

/**
 * @author Origin | May, 29, 2021, 11:13
 * 
 */
public class ClaimVoteCommand implements Command {

    private long lastCommandUsed;

    @Override
    public void execute(Player player, String command, String[] parts) {
        if (player.lastVoteClaim > System.currentTimeMillis()) {
            player.getPacketSender().sendMessage("You can only claim votes every 30 seconds. You need to wait another "+((System.currentTimeMillis() - player.lastVoteClaim) / 1_000)+" Seconds.");
            return;
        }
        new Thread(new Vote(player)).start();

    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
