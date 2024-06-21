package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;
import com.cryptic.services.database.voting.VoteRecord;
import com.cryptic.utility.Color;
import com.teamgames.endpoints.vote.VoteEndpoint;
import com.teamgames.endpoints.vote.obj.ClaimReward;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class VoteRewardCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(VoteCommand.class);

    @Override
    public void execute(Player player, String command, String[] parts) {
        final String name = player.getUsername().toLowerCase();
        final String key = "EH1jU4frnPjyy4AsvqB9W2I3cvH4VwTpSdVPmallSWGKSPDIlI9PAUC2CWA49Gv319EWci1L";
        final VoteEndpoint vote = (new VoteEndpoint()).setApiKey(key).setPlayerName(name).setRewardId("1").setAmount("all");
        if (vote != null) {
            try {
                final ClaimReward[] rewards = vote.getReward();

                if (rewards == null) {
                    logger.info("Voting Rewards Returned Null.");
                    return;
                }

                final List<VoteRecord> records = new ArrayList<>();

                for (var reward : rewards) {
                    if (reward.getMessage() != null && reward.getMessage().contains("Insufficient Points")) continue;
                    final VoteRecord rewardRecord = new VoteRecord(reward.rewardId, reward.giveAmount);
                    records.add(rewardRecord);
                }

                if (records.isEmpty()) {
                    player.message(Color.RED.wrap("You do not have any votes to claim."));
                    return;
                }

                for (var record : records) {
                    final Item reward = new Item(record.itemId(), record.amount());
                    player.getInventory().addOrBank(reward);
                }

                final Item coins = new Item(995, 875_000);
                player.getInventory().addOrBank(coins);

                AchievementsManager.activate(player, Achievements.VOTE_I, 1);
                AchievementsManager.activate(player, Achievements.VOTE_II, 1);
                AchievementsManager.activate(player, Achievements.VOTE_III, 1);
                AchievementsManager.activate(player, Achievements.VOTE_IV, 1);
                player.message(Color.GREEN.wrap("Thank you for voting for Valor!"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
