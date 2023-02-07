package com.aelous.model.content.achievements;

import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;

import java.util.HashMap;

/**
 * @author PVE
 * @Since juli 08, 2020
 */
public class AchievementButtons {

    private static final HashMap<Integer, Achievements> BUTTONS_1 = new HashMap<>();
    private static final HashMap<Integer, Achievements> BUTTONS_2 = new HashMap<>();
    private static final HashMap<Integer, Achievements> BUTTONS_3 = new HashMap<>();

    static {
        int button;
        button = 39431;

        for (final Achievements achievement : Achievements.asList(Difficulty.EASY)) {
            BUTTONS_1.put(button++, achievement);
        }
        button = 39431;
        for (final Achievements achievement : Achievements.asList(Difficulty.MED)) {
            BUTTONS_2.put(button++, achievement);
        }
        button = 39431;
        for (final Achievements achievement : Achievements.asList(Difficulty.HARD)) {
            BUTTONS_3.put(button++, achievement);
        }
    }

    public static boolean handleButtons(Player player, int buttonId) {
        if (buttonId == 39408) {
            AchievementWidget.open(player, Difficulty.EASY);
            AchievementWidget.sendInterfaceForAchievement(player, Achievements.AMPUTEE_ANNIHILATION_I);
            player.putAttrib(AttributeKey.ACHIEVEMENT_DIFFICULTY, Difficulty.EASY);
            player.getPacketSender().sendConfig(1160, 1);
            player.getPacketSender().sendConfig(1161, 0);
            player.getPacketSender().sendConfig(1162, 0);
            player.getPacketSender().setClickedText(39431, true);
            player.getInterfaceManager().open(39400);
            return true;
        }

        if (buttonId == 39409) {
            AchievementWidget.open(player, Difficulty.MED);
            AchievementWidget.sendInterfaceForAchievement(player, Achievements.AMPUTEE_ANNIHILATION_II);
            player.putAttrib(AttributeKey.ACHIEVEMENT_DIFFICULTY, Difficulty.MED);
            player.getPacketSender().sendConfig(1160, 0);
            player.getPacketSender().sendConfig(1161, 1);
            player.getPacketSender().sendConfig(1162, 0);
            player.getPacketSender().setClickedText(39431, true);
            player.getInterfaceManager().open(39400);
            return true;
        }

        if (buttonId == 39410) {
            AchievementWidget.open(player, Difficulty.HARD);
            AchievementWidget.sendInterfaceForAchievement(player, Achievements.AMPUTEE_ANNIHILATION_III);
            player.putAttrib(AttributeKey.ACHIEVEMENT_DIFFICULTY, Difficulty.HARD);
            player.getPacketSender().sendConfig(1160, 0);
            player.getPacketSender().sendConfig(1161, 0);
            player.getPacketSender().sendConfig(1162, 1);
            player.getPacketSender().setClickedText(39431, true);
            player.getInterfaceManager().open(39400);
            return true;
        }
        if (player.getAttribOr(AttributeKey.ACHIEVEMENT_DIFFICULTY, null) == Difficulty.EASY && BUTTONS_1.containsKey(buttonId)) {
            AchievementWidget.sendInterfaceForAchievement(player, BUTTONS_1.get(buttonId));
            player.getPacketSender().setClickedText(buttonId, true);
            return true;
        }

        if (player.getAttribOr(AttributeKey.ACHIEVEMENT_DIFFICULTY, null) == Difficulty.MED && BUTTONS_2.containsKey(buttonId)) {
            AchievementWidget.sendInterfaceForAchievement(player, BUTTONS_2.get(buttonId));
            player.getPacketSender().setClickedText(buttonId, true);
            return true;
        }

        if (player.getAttribOr(AttributeKey.ACHIEVEMENT_DIFFICULTY, null) == Difficulty.HARD && BUTTONS_3.containsKey(buttonId)) {
            AchievementWidget.sendInterfaceForAchievement(player, BUTTONS_3.get(buttonId));
            player.getPacketSender().setClickedText(buttonId, true);
            return true;
        }
        return false;
    }
}
