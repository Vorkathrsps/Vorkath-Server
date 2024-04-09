package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.GameEngine;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VoteRewardCommand implements Command {
    private static final Logger logger = LoggerFactory.getLogger(VoteCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        final String name = player.getUsername().toLowerCase();
        final String id = parts[1] != null ? parts[1] : "1";
        final String amount = parts.length == 3 ? parts[2] : "1";
        final String key = "1xqEgV4QtvySYVIXyXLOm22aNn2TIBoB75ojmAHbvj2slniJbKt7RZVO8vCTFn1rqmcKsCy5";
        GameEngine.getInstance().addSyncTask(() -> {
            try {
                final com.everythingrs.vote.Vote[] reward = com.everythingrs.vote.Vote.reward(key, name, id, amount);
                final String message = reward[0].message;
                if (message != null) {
                    player.message(Color.RED.wrap("You do not have any votes to claim."));
                    return;
                }
                int points = player.<Integer>getAttribOr(AttributeKey.VOTE_POINTS, 0);
                for (var request : reward) {
                    final int itemId = request.reward_id;
                    final int itemAmount = request.give_amount;
                    final Item item = new Item(itemId, itemAmount);
                    points += request.vote_points;
                    player.getInventory().addOrBank(item);
                }
                player.putAttrib(AttributeKey.VOTE_POINTS, points);
                player.message(Color.BLUE.wrap("<img=2018><shad=0>Thank you for supporting Valor! You now have a total of " + reward[0].vote_points + " Vote points.</shad></img>"));
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
