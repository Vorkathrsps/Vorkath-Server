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

    public static void open(Player player) {
        final List<Achievements> list = Arrays.stream(Achievements.VALUES).filter(Objects::nonNull).toList();

        int totalAchievements = list.size();

        player.getPacketSender().sendScrollbarHeight(AchievementUtility.ACHIEVEMENT_SCROLL_BAR, list.size());

        for (int index = 0; index < totalAchievements; index += 4) {
            player.getPacketSender().sendString(ACHIEVEMENTS_LIST_START_ID + index, "");
        }

        int step = 0;
        updateFull(player, list, step);
    }

    public static void updateFull(Player player, List<Achievements> list, int step) {
        for (final Achievements achievement : list.subList(0, Math.min(list.size(), 100))) {
            int completed = player.achievements().get(achievement);
            int progress = (int) (completed * 100 / (double) achievement.getCompleteAmount());
            if (completed > achievement.getCompleteAmount()) {
                completed = achievement.getCompleteAmount();
            }
            int totalAmount = achievement.getCompleteAmount();
            if (progress > 100) {
                progress = 100;
            }
            player.getPacketSender().sendString(achievement.child, achievement.getDescription());
            player.getPacketSender().sendString(ACHIEVEMENTS_LIST_START_ID + step, getColor(completed, totalAmount) + achievement.getName());
            player.getPacketSender().sendString(AchievementUtility.ACHIEVEMENT_PROGRESS_ID + step, progress + "%");
            player.getPacketSender().sendProgressBar(AchievementUtility.PROGRESS_BAR_CHILD + step, progress);
            step += 4;
        }
    }

    public static void openEasyJournal(Player player) {
        AchievementWidget.open(player);
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
