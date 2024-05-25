package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.GameEngine;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;
import com.cryptic.utility.CustomItemIdentifiers;
import com.everythingrs.vote.Vote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoteRewardCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(VoteCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        final String name = player.getUsername().toLowerCase();
        final String key = "EH1jU4frnPjyy4AsvqB9W2I3cvH4VwTpSdVPmallSWGKSPDIlI9PAUC2CWA49Gv319EWci1L";
        GameEngine.getInstance().addSyncTask(() -> {
            try {
                final Vote[] reward = Vote.reward(key, name, "1", "all");
                if (reward[0].message != null) {
                    player.message(Color.RED.wrap("You do not have any votes to claim."));
                    return;
                }
                final Item ticket = new Item(CustomItemIdentifiers.VOTE_TICKET, 1);
                final Item coins = new Item(995, 500_000);
                for (int index = 0; index < reward[0].vote_points; index++) {
                    player.getInventory().addOrBank(coins);
                    player.getInventory().addOrBank(ticket);
                }
            } catch (Throwable e) {
                logger.error("Error During Vote Thread Processing: ", e);
            }
        });
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
