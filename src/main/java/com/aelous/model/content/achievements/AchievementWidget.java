package com.aelous.model.content.achievements;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import com.aelous.utility.Utils;

import java.util.List;

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
        final List<Achievements> list = Achievements.asList(difficulty);

        int totalAchievements = list.size();

        switch (difficulty) {
            case EASY -> player.getPacketSender().sendScrollbarHeight(AchievementUtility.ACHIEVEMENT_SCROLL_BAR, 1000);
            case MED -> player.getPacketSender().sendScrollbarHeight(AchievementUtility.ACHIEVEMENT_SCROLL_BAR, 980);
            case HARD -> player.getPacketSender().sendScrollbarHeight(AchievementUtility.ACHIEVEMENT_SCROLL_BAR, 1160);
        }

        player.getPacketSender().sendString(AchievementUtility.ACHIEVEMENTS_COMPLETED, "Achievements Completed (" + player.achievementsCompleted() + "/" + Achievements.getTotal() + ")");
        //Clear out old achievements
        for (int index = 0; index < 100; index++) {
            player.getPacketSender().sendString(AchievementUtility.ACHIEVEMENTS_LIST_START_ID + index, "");
        }
        for (int index = 0; index < totalAchievements; index++) {
            final Achievements achievement = list.get(index);
            int completed = player.achievements().get(achievement);
            if (completed > achievement.getCompleteAmount()) {
                completed = achievement.getCompleteAmount();
            }
            int totalAmount = achievement.getCompleteAmount();
            player.getPacketSender().sendString(AchievementUtility.ACHIEVEMENTS_LIST_START_ID + index, "" + getColor(completed, totalAmount) + achievement.getName());
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
