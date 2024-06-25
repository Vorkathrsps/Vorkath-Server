package com.cryptic.model.content.achievements;

import com.cryptic.model.World;
import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.rights.PlayerRights;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Color;
import com.cryptic.utility.ItemIdentifiers;
import com.cryptic.utility.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

/**
 * @author PVE
 * @Since juli 08, 2020
 */
public class AchievementsManager {

    private static final Logger logger = LogManager.getLogger(AchievementsManager.class);

    public static final int[] EXPLORER_RINGS = new int[]{ItemIdentifiers.EXPLORERS_RING_1, ItemIdentifiers.EXPLORERS_RING_2, ItemIdentifiers.EXPLORERS_RING_3, ItemIdentifiers.EXPLORERS_RING_4};

    public static void activate(final Player player, final Achievements achievement, final int increaseBy) {

        if (player.getUsername().equalsIgnoreCase("Box test")) return;
        if (player.getParticipatingTournament() != null) return;

        final int current = player.achievements().computeIfAbsent(achievement, _ -> 0);

        if (current >= achievement.getCompleteAmount()) return;


        player.achievements().put(achievement, current + increaseBy);

        final List<Achievements> list = Arrays.stream(Achievements.VALUES).filter(Objects::nonNull).toList();

        checkPreviousAchievementPointsClaimed(player);

        int step = 0;
        AchievementWidget.updateFull(player, list, step);
        if (player.achievements().get(achievement) >= achievement.getCompleteAmount()) {
            int achievementsCompleted = player.getAttribOr(AttributeKey.ACHIEVEMENTS_COMPLETED, 0);
            player.putAttrib(AttributeKey.ACHIEVEMENTS_COMPLETED, achievementsCompleted);

            //When achievements complete, check if we can complete the COMPLETIONIST achievement.
            if (player.completedAllAchievements()) {
                AchievementsManager.activate(player, Achievements.COMPLETIONIST, 1);
            }

            player.message("<col=297A29>Congratulations! You have completed the " + achievement.getName() + " achievement.");
            World.getWorld().sendWorldMessage(format("<img=1953>[<col=" + Color.MEDRED.getColorValue() + ">Achievement</col>]: %s just completed the %s achievement.", (PlayerRights.getCrown(player) + player.getUsername()), achievement.getName()));

            Item[] reward = achievement.getReward();

            if (reward != null) {
                incrementAchievementPoints(player, achievement);
                player.inventory().addOrBank(reward.clone());
                Utils.sendDiscordInfoLog(player.getUsername() + " has completed " + achievement.getName() + " and got " + Arrays.toString(reward.clone()), "achievements");
            }
        }
    }

    private static void incrementAchievementPoints(final Player player, final Achievements achievements) {
        int currentAchievementPoints = player.getAttribOr(AttributeKey.ACHIEVEMENT_POINTS, 0);
        currentAchievementPoints += 3;
        player.putAttrib(AttributeKey.ACHIEVEMENT_POINTS, currentAchievementPoints);
        player.sendInformationMessage("You've received 3 Achievement points for completing the " + achievements.getName() + " achievement.");
        player.sendInformationMessage("You now have a total of " + currentAchievementPoints + " Achievement points.");
    }

    public static void checkPreviousAchievementPointsClaimed(final Player player) {
        final boolean claimedPreviousPoints = player.getAttribOr(AttributeKey.CLAIMED_PREVIOUS_ACHIEVEMENT_POINTS, false);
        int currentAchievementPoints = player.getAttribOr(AttributeKey.ACHIEVEMENT_POINTS, 0);
        if (!claimedPreviousPoints) {
            int count = 0;
            for (final Achievements achievement : player.achievements().keySet()) {
                if (achievement == null) continue;
                if (player.achievements().get(achievement).equals(achievement.getCompleteAmount())) {
                    count++;
                }
            }
            currentAchievementPoints += count;
            player.putAttrib(AttributeKey.ACHIEVEMENT_POINTS, currentAchievementPoints);
            player.putAttrib(AttributeKey.CLAIMED_PREVIOUS_ACHIEVEMENT_POINTS, true);
        }
    }

    public static void handle(final String name, final Player killer) {
        final String nameLowerCase = name.toLowerCase();
        if (nameLowerCase.contains("rock crab")) {
            handleRockCrabAchievements(killer);
        } else if (nameLowerCase.contains("vorkath")) {
            handleVorkathAchievements(killer);
        } else if (nameLowerCase.contains("green dragon")) {
            handleDragonSlayerOneAchievement(killer);
        } else if (nameLowerCase.contains("black dragon") && !nameLowerCase.contains("king black dragon")) {
            handleDragonSlayerTwoAchievement(killer);
        } else if (nameLowerCase.contains("k'ril tsutsaroth") || nameLowerCase.contains("general graardor") || nameLowerCase.contains("commander zilyana") || nameLowerCase.contains("kree'arra")) {
            handleGodwarsAchievement(killer);
        } else if (nameLowerCase.contains("revenant")) {
            handleRevenantsAchievement(killer);
        } else if (nameLowerCase.contains("alchemical hydra")) {
            handleHydraAchievement(killer);
        } else if (nameLowerCase.contains("king black dragon")) {
            handleDragonSlayerTwoAchievement(killer);
            handleDragonSlayerThreeAchievement(killer);
        } else if (nameLowerCase.contains("zulrah")) {
            handleZulrahAchievement(killer);
        } else if (nameLowerCase.contains("scurrius")) {
            handleScurriusAchievement(killer);
        } else if (nameLowerCase.contains("dharok") || nameLowerCase.contains("ahrim") || nameLowerCase.contains("verac") || nameLowerCase.contains("torag") || nameLowerCase.contains("karil") || nameLowerCase.contains("guthan")) {
            handleBarrowsBrotherAchievement(killer);
        } else if (nameLowerCase.equalsIgnoreCase("kraken")) {
            handleKrakenAchievement(killer);
        } else if (nameLowerCase.contains("callisto") || nameLowerCase.contains("vet'ion") || nameLowerCase.contains("venenatis") || nameLowerCase.contains("chaos elemental") || nameLowerCase.contains("scorpia")) {
            handleWildernessBossAchievement(killer);
        }
    }

    private static void handleWildernessBossAchievement(Player killer) {
        AchievementsManager.activate(killer, Achievements.RUN_THE_WILD, 1);
    }

    public static void handleDragonSlayerThreeAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_III, 1);
    }

    public static void handleDragonSlayerTwoAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_II, 1);
    }

    public static void handleDragonSlayerOneAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.DRAGON_SLAYER_I, 1);
    }

    public static void handleKrakenAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.WHATS_KRAKEN, 1);
    }

    public static void handleHydraAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.HYDRATE, 1);
    }

    public static void handleVorkathAchievements(final Player killer) {
        AchievementsManager.activate(killer, Achievements.VORKY_I, 1);
        AchievementsManager.activate(killer, Achievements.VORKY_II, 1);
    }

    public static void handleRockCrabAchievements(final Player killer) {
        AchievementsManager.activate(killer, Achievements.CRABBY_1, 1);
        AchievementsManager.activate(killer, Achievements.CRABBY_2, 1);
        AchievementsManager.activate(killer, Achievements.CRABBY_3, 1);
    }

    public static void handleRevenantsAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.REVENANT_HUNTER_I, 1);
        AchievementsManager.activate(killer, Achievements.REVENANT_HUNTER_II, 1);
        AchievementsManager.activate(killer, Achievements.REVENANT_HUNTER_III, 1);
        AchievementsManager.activate(killer, Achievements.REVENANT_HUNTER_IV, 1);
    }

    public static void handleGodwarsAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.GODWARS_I, 1);
        AchievementsManager.activate(killer, Achievements.GODWARS_II, 1);
        AchievementsManager.activate(killer, Achievements.GODWARS_III, 1);
    }

    public static void handleScurriusAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.SCURRIUS_I, 1);
        AchievementsManager.activate(killer, Achievements.SCURRIUS_II, 1);
        AchievementsManager.activate(killer, Achievements.SCURRIUS_III, 1);
    }

    public static void handleNexAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.NEX_I, 1);
        AchievementsManager.activate(killer, Achievements.NEX_II, 1);
        AchievementsManager.activate(killer, Achievements.NEX_III, 1);
    }

    public static void handleZulrahAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.SNAKE_CHARMER_I, 1);
        AchievementsManager.activate(killer, Achievements.SNAKE_CHARMER_II, 1);
        AchievementsManager.activate(killer, Achievements.SNAKE_CHARMER_III, 1);
    }

    public static void handleBarrowsBrotherAchievement(final Player killer) {
        AchievementsManager.activate(killer, Achievements.BARROWS_I, 1);
        AchievementsManager.activate(killer, Achievements.BARROWS_II, 1);
        AchievementsManager.activate(killer, Achievements.BARROWS_III, 1);
        AchievementsManager.activate(killer, Achievements.BARROWS_IV, 1);
        AchievementsManager.activate(killer, Achievements.BARROWS_V, 1);
    }

    public static boolean isCompleted(Player player, Achievements achievement) {
        return player.achievements().get(achievement) >= achievement.getCompleteAmount();
    }
}
