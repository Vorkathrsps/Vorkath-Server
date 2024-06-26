package com.cryptic.model.entity.player.commands.impl.players;

import com.cryptic.model.content.achievements.Achievements;
import com.cryptic.model.content.achievements.AchievementsManager;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.commands.Command;
import com.cryptic.model.items.Item;
import com.cryptic.services.database.voting.VoteRecord;
import com.cryptic.utility.Color;
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

    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }
}
