package com.cryptic.model.entity.player;

import com.cryptic.GameServer;
import com.cryptic.model.World;
import com.cryptic.model.content.tournaments.TournamentManager;
import com.cryptic.model.entity.attributes.AttributeKey;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class QuestTabUtils {
    public static String getFormattedTournamentTime() {
        long seconds = TournamentManager.timeTillNext();

        if (seconds >= 3600) {
            long hours = seconds / 3600;
            return String.format("%d hour%s", hours, hours > 1 ? "s" : "");
        } else {
            long minutes = seconds / 60;
            return String.format("%d minute%s", minutes, minutes > 1 ? "s" : "");
        }
    }

    public static String getFormattedServerTime() {
        Calendar calendar = World.getWorld().getCalendar();

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        return timeFormat.format(calendar.getTime());
    }

    public static String fetchUpTime() {
        final long upTime = System.currentTimeMillis() - GameServer.startTime;
        return String.format("%d hrs, %d mins",
            TimeUnit.MILLISECONDS.toHours(upTime),
            TimeUnit.MILLISECONDS.toMinutes(upTime) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(upTime)));
    }

    public static String getTimeDHS(Player player) {
        long gameTime = player.getAttribOr(AttributeKey.GAME_TIME, 0L);
        int time = (int) (gameTime * 0.6);

        int days = time / 86400;
        int remainingSeconds = time % 86400;
        int hours = remainingSeconds / 3600;
        int minutes = (remainingSeconds % 3600) / 60;

        StringBuilder timeString = new StringBuilder();

        if (days > 0) {
            timeString.append(days).append(" day").append(days > 1 ? "s" : "");
            if (hours > 0)
                timeString.append(", ").append(hours).append(" hour").append(hours > 1 ? "s" : "");
        } else {
            if (hours > 0)
                timeString.append(hours).append(" hour").append(hours > 1 ? "s" : "");
            if (minutes > 0)
                timeString.append(", ").append(minutes).append(" minute").append(minutes > 1 ? "s" : "");
        }

        return timeString.toString();
    }

    public static String formatNumberWithSuffix(long number) {
        if (number < 1000) {
            return String.valueOf(number);
        } else if (number < 1_000_000) {
            return String.format("%.1fK", number / 1000.0);
        } else if (number < 1_000_000_000) {
            return String.format("%.1fM", number / 1_000_000.0);
        } else {
            return String.format("%.1fB", number / 1_000_000_000.0);
        }
    }
}
