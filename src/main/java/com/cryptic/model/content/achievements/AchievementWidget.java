package com.cryptic.model.content.achievements;

import com.cryptic.model.entity.attributes.AttributeKey;
import com.cryptic.model.entity.player.Player;
import com.cryptic.utility.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.cryptic.model.content.achievements.AchievementUtility.ACHIEVEMENTS_LIST_START_ID;

/**
 * @author PVE
 * @Since juli 08, 2020
 */
public class AchievementWidget {

    public static void sendInterfaceForAchievement(final Player player, Achievements achievement) {
        final int completed = player.achievements().get(achievement);
        final int progress = (int) (completed * 100 / (double) achievement.getCompleteAmount());

        player.getPacketSender().sendString(AchievementUtility.ACHIEVEMENT_NAME_ID, "<col=ff9040>" + achievement.getName());
        player.getPacketSender().sendString(AchievementUtility.ACHIEVEMENT_PROGRESS_ID, "<col=ffffff>Progress:</col><col=ffffff>" + " (" + progress + "%) " + Utils.format(completed) + " / " + Utils.format(achievement.getCompleteAmount()));
        player.getPacketSender().sendProgressBar(AchievementUtility.PROGRESS_BAR_CHILD, progress);
        player.getPacketSender().sendString(AchievementUtility.ACHIEVEMENT_DESCRIPTION_ID, "<col=ffffff>" + achievement.getDescription());
        player.getPacketSender().sendItemOnInterface(AchievementUtility.CONTAINER_ID, achievement.getReward());
        String rewardString = achievement.otherRewardString();
        if (rewardString.isEmpty()) {
            player.getPacketSender().sendString(AchievementUtility.REWARD_STRING, "");//Empty string
        } else {
            player.getPacketSender().sendString(AchievementUtility.REWARD_STRING, rewardString);
        }
    }

    public static void open(Player player, Difficulty difficulty) {
        final List<Achievements> list = Arrays.stream(Achievements.values()).filter(Objects::nonNull).toList();

        int totalAchievements = list.size();

        switch (difficulty) {
            case EASY -> player.getPacketSender().sendScrollbarHeight(AchievementUtility.ACHIEVEMENT_SCROLL_BAR, 1000);
            case MED -> player.getPacketSender().sendScrollbarHeight(AchievementUtility.ACHIEVEMENT_SCROLL_BAR, 980);
            case HARD -> player.getPacketSender().sendScrollbarHeight(AchievementUtility.ACHIEVEMENT_SCROLL_BAR, 1160);
        }

        for (int index = 0; index < totalAchievements; index += 4) {
            player.getPacketSender().sendString(ACHIEVEMENTS_LIST_START_ID + index, "");
        }

        int step = 0;
        System.out.println("loop "+list.size());
        for (final Achievements achievement : list.subList(0, Math.min(list.size(), 100))) {
            int completed = player.achievements().get(achievement);
            final int progress = (int) (completed * 100 / (double) achievement.getCompleteAmount());
            if (completed > achievement.getCompleteAmount()) {
                completed = achievement.getCompleteAmount();
            }
            int totalAmount = achievement.getCompleteAmount();
            player.getPacketSender().sendString(ACHIEVEMENTS_LIST_START_ID + step, getColor(completed, totalAmount) + achievement.getName());
            player.getPacketSender().sendString(AchievementUtility.ACHIEVEMENT_PROGRESS_ID + step, progress + "%");
            player.getPacketSender().sendProgressBar(AchievementUtility.PROGRESS_BAR_CHILD + step, progress);
            step += 4;
        }
    }

    public static void openEasyJournal(Player player) {
        AchievementWidget.open(player, Difficulty.EASY);
        AchievementWidget.sendInterfaceForAchievement(player, Achievements.AMPUTEE_ANNIHILATION_I);
        player.putAttrib(AttributeKey.ACHIEVEMENT_DIFFICULTY, Difficulty.EASY);
        player.getPacketSender().sendConfig(1160, 1);
        player.getPacketSender().sendConfig(1161, 0);
        player.getPacketSender().sendConfig(1162, 0);
        player.getPacketSender().setClickedText(39431, true);
        player.getInterfaceManager().open(39400);
    }

    private static String getColor(int amount, int max) {
        if (amount == 0) {
            return AchievementUtility.RED;
        }
        if (amount >= max) {
            return AchievementUtility.GREEN;
        }
        return AchievementUtility.ORANGE;
    }
}
