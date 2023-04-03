package com.aelous.utility;

import com.aelous.GameServer;
import com.aelous.model.World;
import com.google.gson.Gson;
import com.aelous.GameEngine;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.Entity;
import com.aelous.model.entity.player.IronMode;
import com.aelous.model.entity.player.Player;
import com.aelous.model.items.Item;
import com.aelous.model.map.position.Tile;
import com.aelous.network.Session;
import com.aelous.utility.flood.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.text.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Utils {

    public static final Pattern VALID_NAME = Pattern.compile("^[a-zA-Z0-9_ ]{1,12}$");

    private static final Logger logger = LogManager.getLogger(Utils.class);

    /** The time correction. */
    private static long timeCorrection;

    /** The last time update. */
    private static long lastTimeUpdate;

    public static synchronized long currentTimeMillis() {
        long l = System.currentTimeMillis();
        if (l < lastTimeUpdate)
            timeCorrection += lastTimeUpdate - l;
        lastTimeUpdate = l;
        return l + timeCorrection;
    }

    /**
     * Gets the coord offsets near.
     *
     * @param size the size
     * @return the coord offsets near
     */
    public static int[][] getCoordOffsetsNear(int size) {
        int[] xs = new int[4 + (4 * size)];
        int[] xy = new int[xs.length];
        xs[0] = -size;
        xy[0] = 1;
        xs[1] = 1;
        xy[1] = 1;
        xs[2] = -size;
        xy[2] = -size;
        xs[3] = 1;
        xy[2] = -size;
        for (int fakeSize = size; fakeSize > 0; fakeSize--) {
            xs[(4 + ((size - fakeSize) * 4))] = -fakeSize + 1;
            xy[(4 + ((size - fakeSize) * 4))] = 1;
            xs[(4 + ((size - fakeSize) * 4)) + 1] = -size;
            xy[(4 + ((size - fakeSize) * 4)) + 1] = -fakeSize + 1;
            xs[(4 + ((size - fakeSize) * 4)) + 2] = 1;
            xy[(4 + ((size - fakeSize) * 4)) + 2] = -fakeSize + 1;
            xs[(4 + ((size - fakeSize) * 4)) + 3] = -fakeSize + 1;
            xy[(4 + ((size - fakeSize) * 4)) + 3] = -size;
        }
        return new int[][] { xs, xy };
    }

    public static String gameModeToString(Player player) {
        //Player is some sort of ironman
        if (player.getIronManStatus() != IronMode.NONE) {
            return player.getIronManStatus().name;
        } else {
            return player.getGameMode().toName();
        }
    }

    public static String convertMsToTime(long ms) {
        if (ms >= 600000)
            return String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(ms),
                TimeUnit.MILLISECONDS.toMinutes(ms) -
                    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)), // The change is in this line
                TimeUnit.MILLISECONDS.toSeconds(ms) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
        return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(ms) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(ms)), TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
    }

    public static String displayTime(long startDate) {

        //milliseconds
        long different = System.currentTimeMillis() - startDate;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        return String.format("%dD %dH %dM %dS",
            elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);

    }

    public static String getCalanderDate(long datePurchased) {
        LocalDateTime date = LocalDateTime.ofInstant(Instant.ofEpochMilli(datePurchased), ZoneId.systemDefault());
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String formatNumber(int value) {
        return new DecimalFormat().format(value);
    }

    public static int toCyclesOrDefault(long time, int def, TimeUnit unit) {
        if (time > Integer.MAX_VALUE) {
            time = def;
        }
        return (int) TimeUnit.MILLISECONDS.convert(time, unit) / 600;
    }

    public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortEntries(Map<K, V> map) {
        List<Map.Entry<K, V>> sortedEntries = new ArrayList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(sortedEntries, (e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        return sortedEntries;
    }

    public static Map<String, Long> sortByComparator(Map<String, Long> unsortMap, final boolean ascending) {
        List<Map.Entry<String, Long>> list = new LinkedList<Map.Entry<String, Long>>(unsortMap.entrySet());
        // Sorting the list based on values
        list.sort(new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1,
                               Map.Entry<String, Long> o2) {
                if (ascending) {
                    return o1.getValue().compareTo(o2.getValue());
                } else {
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });
        // Maintaining insertion order with the help of LinkedList
        Map<String, Long> sortedMap = new LinkedHashMap<String, Long>();
        for (Map.Entry<String, Long> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static <T> List<T> jsonArrayToList(Path path, Class<T[]> clazz) {
        try {
            T[] collection = new Gson().fromJson(Files.newBufferedReader(path), clazz);
            return new ArrayList<T>(Arrays.asList(collection));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getFractionFromPercentage(float percentage) {
        return "1 / " + (int) Math.round((1 / percentage) * 100); //Was ceil is now round
    }

    public static int getDenominatorFromPercentage(float percentage) {
        return (int) Math.round((1 / percentage) * 100); //Was ceil is now round
    }

    /**
     * Useful method for getting the stack trace where some code
     * was executed without throwing an exception.
     * This method basically makes it so that we don't have to throw new RuntimeException
     * in order to get a stack trace.
     *
     * @return the stack trace
     */
    public static String getStackTrace() {
        final StringWriter writer = new StringWriter();
        new Exception("Stack trace").printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    public static String getStackTraceForDiscord(int characterLimit) {
        final StringWriter writer = new StringWriter();
        new Exception("Stack trace").printStackTrace(new PrintWriter(writer));
        final String stackTrace = writer.toString().replaceAll("\n", " ").replaceAll("\\$", ":").replaceAll("\\<", "(").replaceAll("\\>", ")").replaceAll("\t", " ");
        return stackTrace.substring(0, Math.min(stackTrace.length() - 1, characterLimit));
    }

    public static String getStackTraceForDiscord(Exception e, int characterLimit) {
        final StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        final String stackTrace = writer.toString().replaceAll("\n", " ").replaceAll("\\$", ":").replaceAll("\\<", "(").replaceAll("\\>", ")").replaceAll("\t", " ");
        return stackTrace.substring(0, Math.min(stackTrace.length() - 1, characterLimit));
    }

    public static String getStackTraceForDiscord(Throwable t, int characterLimit) {
        final StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        final String stackTrace = writer.toString().replaceAll("\n", " ").replaceAll("\\$", ":").replaceAll("\\<", "(").replaceAll("\\>", ")").replaceAll("\t", " ");
        return stackTrace.substring(0, Math.min(stackTrace.length() - 1, characterLimit));
    }

    public static String getStackTraceForDiscord(Error e, int characterLimit) {
        final StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        final String stackTrace = writer.toString().replaceAll("\n", " ").replaceAll("\\$", ":").replaceAll("\\<", "(").replaceAll("\\>", ")").replaceAll("\t", " ");
        return stackTrace.substring(0, Math.min(stackTrace.length() - 1, characterLimit));
    }

    public static String getStackTraceForDiscord(Throwable t) {
        return getStackTraceForDiscord(t, 1990);
    }

    public static String getStackTraceForDiscord(Error e) {
        return getStackTraceForDiscord(e, 1990);
    }

    public static String getStackTraceForDiscord(Exception e) {
        return getStackTraceForDiscord(e, 1990);
    }

    public static String getStackTraceForDiscord() {
        return getStackTraceForDiscord(1990);
    }

    public static String getDiscordWebhookHeader() {
        return "[" + Utils.getCurrentServerDateTime() + "][v" + GameServer.properties().gameVersion + "][" + (GameServer.properties().production ? "Live" : "Dev") + "]" + (World.getWorld().getPlayers().get(1) != null ? "[Player1=" + World.getWorld().getPlayers().get(1).getUsername() + "]" : "") + ": ";
    }

    public static void sendDiscordInfoLog(String text, String type) {
        if (GameServer.properties().enableDiscordLogging) {
            GameEngine.getInstance().submitDiscord(() -> {
                try {
                    switch (type) {
                        case "command" -> {
                            GameServer.getCommandWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getCommandWebHook().execute();
                        }
                        case "warning" -> {
                            GameServer.getWarningWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getWarningWebHook().execute();
                        }
                        case "trade" -> {
                            GameServer.getTradeWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getTradeWebHook().execute();
                        }
                        case "stake" -> {
                            GameServer.getStakeWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getStakeWebHook().execute();
                        }
                        case "chat" -> {
                            GameServer.getChatWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getChatWebHook().execute();
                        }
                        case "pm" -> {
                            GameServer.getPmWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getPmWebHook().execute();
                        }
                        case "npcdrops" -> {
                            GameServer.getNpcDropsWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getNpcDropsWebHook().execute();
                        }
                        case "playerdrops" -> {
                            GameServer.getPlayerDropsWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getPlayerDropsWebHook().execute();
                        }
                        case "pickups" -> {
                            GameServer.getPickupsWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getPickupsWebHook().execute();
                        }
                        case "login" -> {
                            GameServer.getLoginWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getLoginWebHook().execute();
                        }
                        case "logout" -> {
                            GameServer.getLogoutWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getLogoutWebHook().execute();
                        }
                        case "sanctions" -> {
                            GameServer.getSanctionsWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getSanctionsWebHook().execute();
                        }
                        case "shops" -> {
                            GameServer.getShopsWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getShopsWebHook().execute();
                        }
                        case "playerdeaths" -> {
                            GameServer.getPlayerDeathsWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getPlayerDeathsWebHook().execute();
                        }
                        case "passwordchange" -> {
                            GameServer.getPasswordChangeWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getPasswordChangeWebHook().execute();
                        }
                        case "tournaments" -> {
                            GameServer.getTournamentWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getTournamentWebHook().execute();
                        }
                        case "referrals" -> {
                            GameServer.getReferralsWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getReferralsWebHook().execute();
                        }
                        case "achievements" -> {
                            GameServer.getAchievementsWebHookWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getAchievementsWebHookWebHook().execute();
                        }
                        case "trading_post_sales" -> {
                            GameServer.getTradingPostSalesWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getTradingPostSalesWebHook().execute();
                        }
                        case "trading_post_purchases" -> {
                            GameServer.getTradingPostPurchasesWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getTradingPostPurchasesWebHook().execute();
                        }
                        case "raids" -> {
                            GameServer.getRaidsWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getRaidsWebHook().execute();
                        }
                        case "starter_box_received" -> {
                            GameServer.getStarterBoxWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getStarterBoxWebHook().execute();
                        }
                        case "clan_box_opened" -> {
                            GameServer.getClanBoxWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getClanBoxWebHook().execute();
                        }
                        case "gamble" -> {
                            GameServer.getGambleWebHook().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getGambleWebHook().execute();
                        }
                        case "box_and_tickets" -> {
                            GameServer.getBoxAndTicketsWebHookUrl().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getBoxAndTicketsWebHookUrl().execute();
                        }
                        case "fpk_merk" -> {
                            GameServer.getFpkMerkwebHookURL().setContent(getDiscordWebhookHeader() + text.replaceAll("\"", ""));
                            GameServer.getFpkMerkwebHookURL().execute();
                        }
                    }
                } catch (IOException e) {
                    if (e.getMessage().contains("HTTP response code: 429")) {
                        //logger.error("Discord Webhook rate limit reached for Info webhook");
                    } else if (e.getMessage().contains("HTTP response code: 400")) {
                        System.err.println("Discord Webhook content was in bad format (probably invalid characters)");
                    } else {
                        logger.error("I think Discord webhook threw an IO exception");
                        //Let's log the text, since this error type shouldn't happen and we don't know why it did.
                        //Perhaps this error is caused by the Discord servers being overloaded/lagging in general?
                        logger.error("The text that Discord tried to log was: " + text);
                        logger.catching(e);
                    }
                } catch (Throwable t) {
                    logger.error("I think Discord webhook threw an exception");
                    logger.catching(t);
                }
            });
        }
    }

    public static void sendDiscordInfoLog(String text) {
        sendDiscordInfoLog(text, "info");
    }

    public static float getPercentageFromDecimal(float decimal) {
        return decimal * 100;
    }

    public static float getPercentageFromDenominator(float denominator) {
        return (1 / denominator) * 100;
    }

    public static float getDecimalFromFraction(int numerator, int denominator) {
        return (float) numerator / denominator;
    }

    public static float getDecimalFromDenominator(int denominator) {
        return (float) 1 / denominator;
    }

    /**
     * Gets the date of server.
     */
    public static String getDate() {
        return new SimpleDateFormat("EE MMM dd yyyy").format(new Date());
    }

    /**
     * Gets the date of server.
     */
    public static String getSimpleDate() {
        return new SimpleDateFormat("yyyy/MM/dd").format(new Date());
    }

    /**
     * Finds the first value in the array that matches the predicate. If none is found, returns the default value.
     *
     * @param array        the array to loop.
     * @param predicate    the predicate to test against each value.
     * @param defaultValue the default value to return if no value matches the predicate.
     * @return a matching value.
     */
    public static <T> T findMatching(final T[] array, final Predicate<T> predicate, final T defaultValue) {
        for (T object : array) {
            if (predicate.test(object)) {
                return object;
            }
        }
        return defaultValue;
    }

    public static String kOrMil(int amount) {
        if (amount < 100_000)
            return String.valueOf(amount);
        if (amount < 10_000_000)
            return amount / 1_000 + "K";
        else
            return amount / 1_000_000 + "M";
    }

    public static int[] convertRollRangeStringToIntArray(String input) {
        String[] strArray = input.split("-");
        int[] intArray = new int[strArray.length];
        for (int i = 0; i < strArray.length; i++) {
            intArray[i] = Integer.parseInt(strArray[i]);
        }
        return intArray;
    }

    /**
     * Finds out if a certain event should happen, and if it should, return
     * true;
     *
     * @param chance The chance of the event happening
     * @return If the event should happen
     */

    public static boolean securedRandomChance(double chance) {
        SecureRandom srand = new SecureRandom();
        return srand.nextDouble() <= chance;
    }
    public static boolean percentageChance(int chance) {
        return RANDOM_GEN.nextInt(100) < chance;
    }

    public static String pluralOrNot(String word, int count) {
        String value = "";
        if (count == 0 || count > 1) {
            value = word + "s";
        } else {
            value = word;
        }
        return value;
    }

    public static int secondsToTicks(int seconds) {
        return (seconds * 1000) / 600;
    }

    public static float ticksToSeconds(int tick) {
        int ticksInMillis = tick * 600;
        float tickToSecs = ticksInMillis / 1000f;
        return tickToSecs;
    }

    public static float ticksToMinutes(int tick) {
        int ticksInMillis = tick * 600;
        float tickToMin = ticksInMillis / 60000f;
        return tickToMin;
    }

    public static float ticksToHours(int tick) {
        int ticksInMillis = tick * 600;
        float tickToHour = ticksInMillis / 3_600_000f;
        return tickToHour;
    }

    public static String convertLongToDateTime(long time) {
        Date date = new Date(time);
        //Format format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Format format = new SimpleDateFormat("dd MMM yyyy h:mm:ss a");
        return format.format(date);
    }

    public static String convertLongToTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("h:mm:ss a");
        return format.format(date);
    }

    public static String convertLongToDate(long time) {
        Date date = new Date(time);
        //Format format = new SimpleDateFormat("yyyy-MM-dd");
        Format format = new SimpleDateFormat("dd MMM yyyy");
        return format.format(date);
    }

    public static String convertLongToDuration(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append((days > 1 || days == 0) ? " days " : " day ");
        sb.append(hours);
        sb.append((hours > 1 || hours == 0) ? " hours " : " hour ");
        sb.append(minutes);
        sb.append((minutes > 1 || minutes == 0) ? " minutes " : " minute ");
        sb.append(seconds);
        sb.append((seconds > 1 || seconds == 0) ? " seconds" : " second");

        return (sb.toString());
    }

    public static String convertLongToShortDuration(long time, boolean useSeconds) {
        if (time < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }
        long days = TimeUnit.MILLISECONDS.toDays(time);
        time -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(time);
        time -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(time);
        time -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(time);


        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append("d ");
        sb.append(hours);
        sb.append("h ");
        sb.append(minutes);
        sb.append("m ");
        if (useSeconds) {
            sb.append(seconds);
            sb.append("s ");
        }


        return (sb.toString());
    }

    public static String convertSecondsToShortDuration(long time, boolean useSeconds) {
        if (time < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = (int) TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - (days * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
        long seconds = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);

        StringBuilder sb = new StringBuilder(64);
        sb.append(days);
        sb.append("d ");
        sb.append(hours);
        sb.append("h ");
        sb.append(minutes);
        sb.append("m ");
        if (useSeconds) {
            sb.append(seconds);
            sb.append("s ");
        }

        return (sb.toString());
    }

    public static String convertSecondsToDuration(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = (int) TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - (days * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
        long seconds = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);

        String sb = hours +
            ((hours > 1 || hours == 0) ? " hours " : " hour ") +
            minutes +
            ((minutes > 1 || minutes == 0) ? " minutes " : " minute ") +
            seconds +
            ((seconds > 1 || seconds == 0) ? " seconds" : " second");
        return (sb);
    }

    public static String convertSecondsToDuration(long time, boolean showDaysAndHours) {
        if (time < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = (int) TimeUnit.SECONDS.toDays(time);
        long hours = TimeUnit.SECONDS.toHours(time) - (days * 24);
        long minutes = TimeUnit.SECONDS.toMinutes(time) - (TimeUnit.SECONDS.toHours(time) * 60);
        long seconds = TimeUnit.SECONDS.toSeconds(time) - (TimeUnit.SECONDS.toMinutes(time) * 60);

        StringBuilder sb = new StringBuilder(64);
        if (showDaysAndHours) {
            sb.append(days);
            sb.append((days > 1 || days == 0) ? " days " : " day ");
            sb.append(hours);
            sb.append((hours > 1 || hours == 0) ? " hours " : " hour ");
            sb.append(minutes);
            sb.append((minutes > 1 || minutes == 0) ? " minutes " : " minute ");
            sb.append(seconds);
            sb.append((seconds > 1 || seconds == 0) ? " seconds" : " second");
        } else {
            sb.append(minutes);
            sb.append((minutes > 1 || minutes == 0) ? " minutes " : " minute ");
            sb.append(seconds);
            sb.append((seconds > 1 || seconds == 0) ? " seconds" : " second");
        }

        return (sb.toString());
    }

    public static int getTicks(int seconds) {
        return (int) (seconds / 0.6);
    }

    public static int getSeconds(int ticks) {
        return (int) (ticks * 0.6);
    }

    //Our formatter
    public static final DecimalFormat FORMATTER = new DecimalFormat("0.#");

    /**
     * Random instance, used to generate pseudo-random primitive types.
     */
    public static final SecureRandom RANDOM_GEN = new SecureRandom();

    public static final RandomGen RANDOM_GEN2 = new RandomGen();

    /**
     * The random instance.
     */
    public static final Random RANDOM = new Random();

    private static ZonedDateTime zonedDateTime;
    public static final int HALF_A_DAY_IN_MILLIS = 43200000;

    /**
     * Returns a number between 0 and length.
     * Warning: set the number to 1 higher than the max value
     * Length is not the same as max number.
     *
     * @param length the length (not max value, but rather size)
     * @return int random number
     */
    public static int getRandom(int length) {
        return RANDOM_GEN.nextInt(length);
    }

    public static double getRandomDouble(double length) {
        return RANDOM_GEN.nextDouble();
    }

    /**
     * Returns a number between 0 and length.
     * Warning: set the number to 1 higher than the max value
     * Length is not the same as max number.
     *
     * @return double random number
     */
    public static double getRandomDouble() {
        return RANDOM_GEN.nextDouble();
    }

    public static int getRandomInt() {
        return RANDOM_GEN.nextInt();
    }

    public static int inclusive(int min, int max) {
        return RANDOM_GEN2.inclusive(min, max);
    }

    public static int randomInclusive(int min, int max) {
        return Math.min(min, max) + RANDOM_GEN.nextInt(Math.max(min, max) - Math.min(min, max) + 1);
    }

    /**
     * Picks a random element out of any array type.
     *
     * @param collection the collection to pick the element from.
     * @return the element chosen.
     */
    public static <T> T randomElement(Collection<T> collection) {
        return new ArrayList<T>(collection).get((int) (RANDOM_GEN.nextDouble() * collection.size()));
    }

    /**
     * Picks a random element out of any list type.
     *
     * @param list the list to pick the element from.
     * @return the element chosen.
     */
    public static <T> T randomElement(List<T> list) {
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get((int) (RANDOM_GEN.nextDouble() * list.size()));
    }

    /**
     * Picks a random element out of any array type.
     *
     * @param array the array to pick the element from.
     * @return the element chosen.
     */
    public static <T> T randomElement(T[] array) {
        return array[(int) (RANDOM_GEN.nextDouble() * array.length)];
    }

    /**
     * Picks a random element out of any array type.
     *
     * @param array the array to pick the element from.
     * @return the element chosen.
     */
    public static int randomElement(int[] array) {
        return array[(int) (RANDOM_GEN.nextDouble() * array.length)];
    }

    public static void compareItemArrays(int[] oldItemsArray, int[] newItemsArray) {
        Integer[] oldItems = Arrays.stream(oldItemsArray).boxed().toArray(Integer[]::new);
        Integer[] newItems = Arrays.stream(newItemsArray).boxed().toArray(Integer[]::new);
        Set<Integer> set1 = new HashSet<>(Arrays.asList(oldItems));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(newItems));

        set1.removeAll(set2);

        for (int itemId : set1) {
            Item item = new Item(itemId);
            //System.out.println("Missing Item " + item.getId() + ": " + item.name());
        }
    }

    public static void displayItemNames(int[] items) {
        for (int itemId : items) {
            Item item = new Item(itemId);
            //System.out.println("Item " + item.getId() + ": " + item.name());
        }
    }

    public static List<Player> getCombinedPlayerList(Player p) {
        List<Player> plrs = new LinkedList<Player>();
        for (Player localPlayer : p.getLocalPlayers()) {
            if (localPlayer != null)
                plrs.add(localPlayer);
        }
        plrs.add(p);
        return plrs;
    }

    public static String getCurrentServerTime() {
        zonedDateTime = ZonedDateTime.now();
        int hour = zonedDateTime.getHour();
        String hourPrefix = hour < 10 ? "0" + hour + "" : "" + hour + "";
        int minute = zonedDateTime.getMinute();
        String minutePrefix = minute < 10 ? "0" + minute + "" : "" + minute + "";
        return "" + hourPrefix + ":" + minutePrefix + "";
    }

    public static String getCurrentServerDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, h:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime());
    }

    public static String getTimePlayed(long totalPlayTime) {
        final int sec = (int) (totalPlayTime / 1000), h = sec / 3600, m = sec / 60 % 60, s = sec % 60;
        return (h < 10 ? "0" + h : h) + ":" + (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
    }

    public static String getHoursPlayed(long totalPlayTime) {
        final int sec = (int) (totalPlayTime / 1000), h = sec / 3600;
        return (h < 10 ? "0" + h : h) + "h";
    }

    public static String asMinutesLeft(int ticksLeft) {
        long ms = ticksLeft * 600;
        int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(ms);
        String str = "";

        if (minutes > 0) {
            if (minutes > 1) {
                str = minutes + " mins";
            } else {
                str = "One min";
            }
        }

        return str;
    }

    public static String asSeconds(int ticksLeft) {
        long ms = ticksLeft * 600;
        int seconds = (int) TimeUnit.MILLISECONDS.toSeconds(ms);
        String str = "";


        if (seconds > 1) {
            str = seconds + " seconds";
        } else {
            str = "One second";
        }

        return str;
    }

    public static int getMinutesPassed(long t) {
        int seconds = (int) ((t / 1000) % 60);
        int minutes = (int) (((t - seconds) / 1000) / 60);
        return minutes;
    }

    public static void addToFile(String fileName, String data) {
        GameEngine.getInstance().submitLowPriority(() -> {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
                try {
                    out.write(data);
                    out.newLine();
                } finally {
                    out.close();
                }
            } catch (IOException e) {
                logger.catching(e);
            }
        });
    }

    public static void deleteFromFile(String file, String name) {
        GameEngine.getInstance().submitLowPriority(() -> {
            try {
                BufferedReader r = new BufferedReader(new FileReader(file));
                ArrayList<String> contents = new ArrayList<String>();
                while (true) {
                    String line = r.readLine();
                    if (line == null) {
                        break;
                    } else {
                        line = line.trim();
                    }
                    if (!line.equalsIgnoreCase(name)) {
                        contents.add(line);
                    }
                }
                r.close();
                BufferedWriter w = new BufferedWriter(new FileWriter(file));
                for (String line : contents) {
                    w.write(line, 0, line.length());
                    w.newLine();
                }
                w.flush();
                w.close();
            } catch (Exception e) {
                logger.catching(e);
            }
        });
    }

    public static Item[] concat(Item[] a, Item[] b) {
        int aLen = a.length;
        int bLen = b.length;
        Item[] c = new Item[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }

    public static Player getCloseRandomPlayer(List<Player> plrs) {
        int index = Utils.getRandom(plrs.size());
        if (index > 0)
            return plrs.get(index);
        return null;
    }

    public static byte directionDeltaX[] = new byte[]{0, 1, 1, 1, 0, -1, -1, -1};
    public static byte directionDeltaY[] = new byte[]{1, 1, 0, -1, -1, -1, 0, 1};
    public static byte xlateDirectionToClient[] = new byte[]{1, 2, 4, 7, 6, 5, 3, 0};

    public static int direction(int srcX, int srcY, int x, int y) {
        double dx = (double) x - srcX, dy = (double) y - srcY;
        double angle = Math.atan(dy / dx);
        angle = Math.toDegrees(angle);
        if (Double.isNaN(angle))
            return -1;
        if (Math.signum(dx) < 0)
            angle += 180.0;
        return (int) ((((90 - angle) / 22.5) + 16) % 16);
        /*int changeX = x - srcX; int changeY = y - srcY;
        for (int j = 0; j < directionDeltaX.length; j++) {
            if (changeX == directionDeltaX[j] &&
                changeY == directionDeltaY[j])
                return j;

        }
        return -1;*/
    }

    public static String ucFirst(String str) {
        str = str.toLowerCase();
        if (str.length() > 1) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return str.toUpperCase();
        }
        return str;
    }

    /**
     * Format number into for example: 357,555
     */
    public static String formatNumber(long number) {
        //System.out.println(number + " -> " + NumberFormat.getNumberInstance().format(number));
        //Even though NumberFormat.getNumberInstance().format(number) is slower it is easier on memory.
        return NumberFormat.getNumberInstance().format(number);
    }

    /**
     * Formats digits for integers.
     *
     * @param amount
     * @return
     */
    public static String format(final int amount) {
        String string = Integer.toString(amount);
        return string;
    }

    /**
     * Formats a number into a string with commas.
     */
    public static String formatValue(int value) {
        return new DecimalFormat("#, ###").format(value);
    }

    /**
     * Formats digits for longs.
     */
    public static String format(final long amount) {
        String string = Long.toString(amount);
        return string;
    }

    /**
     * Formats a price for longs.
     *
     * @param amount
     */
    public static String formatPrice(final int amount) {
        if (amount >= 0 && amount < 1_000) {
            return "" + amount;
        }
        if (amount >= 1_000 && amount < 1_000_000) {
            return (amount / 1_000) + "K";
        }
        if (amount >= 1_000_000 && amount < 1_000_000_000) {
            return (amount / 1_000_000) + "M";
        }
        if (amount >= 1_000_000_000 && amount < Integer.MAX_VALUE) {
            return (amount / 1_000_000_000) + "B";
        }
        return "<col=fc2a2a>Lots!";
    }

    /**
     * Formats a price for longs.
     *
     * @param amount
     */
    public static String formatPrice(final long amount) {
        if (amount >= 0 && amount < 1_000) {
            return "" + amount;
        }
        if (amount >= 1_000 && amount < 1_000_000) {
            return (amount / 1_000) + "K";
        }
        if (amount >= 1_000_000 && amount < 1_000_000_000) {
            return (amount / 1_000_000) + "M";
        }
        if (amount >= 1_000_000_000 && amount < Long.MAX_VALUE) {
            return (amount / 1_000_000_000) + "B";
        }
        return "<col=fc2a2a>Lots!";
    }

    /**
     * @return 5.5 for example.
     */
    public static double roundDoubleToNearestOneDecimalPlace(double number) {
        DecimalFormat df = new DecimalFormat("#.#");
        return Double.parseDouble(df.format(number));
    }

    /**
     * Shorted to 5.66 for example.
     */
    public static double roundDoubleToNearestTwoDecimalPlaces(double number) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(number));
    }

    public static double getDoubleRoundedUp(double doubleNumber) {
        return Math.ceil(doubleNumber);
    }

    public static double getDoubleRoundedDown(double doubleNumber) {
        return (double) ((int) doubleNumber);
    }

    public static String formatRunescapeStyle(long num) {
        boolean negative = false;
        if (num < 0) {
            num = -num;
            negative = true;
        }
        int length = String.valueOf(num).length();
        String number = Long.toString(num);
        String numberString = number;
        String end;
        if (length == 4) {
            numberString = number.charAt(0) + "k";
            //6400
            double doubleVersion;
            doubleVersion = num / 1000.0;
            if (doubleVersion != getDoubleRoundedUp(doubleVersion)) {
                if (num - (1000 * getDoubleRoundedDown(doubleVersion)) > 100) {
                    numberString = number.charAt(0) + "." + number.charAt(1) + "k";
                }
            }
        } else if (length == 5) {
            numberString = number.substring(0, 2) + "k";
        } else if (length == 6) {
            numberString = number.substring(0, 3) + "k";
        } else if (length == 7) {
            String sub = number.substring(1, 2);
            if (sub.equals("0")) {
                numberString = number.charAt(0) + "m";
            } else {
                numberString = number.charAt(0) + "." + number.charAt(1) + "m";
            }
        } else if (length == 8) {
            end = "." + number.charAt(2);
            if (end.equals(".0")) {
                end = "";
            }
            numberString = number.substring(0, 2) + end + "m";
        } else if (length == 9) {
            end = "." + number.charAt(3);
            if (end.equals(".0")) {
                end = "";
            }
            numberString = number.substring(0, 3) + end + "m";
        } else if (length == 10) {
            numberString = number.substring(0, 4) + "m";
        } else if (length == 11) {
            numberString = number.substring(0, 2) + "." + number.substring(2, 5) + "b";
        } else if (length == 12) {
            numberString = number.substring(0, 3) + "." + number.substring(3, 6) + "b";
        } else if (length == 13) {
            numberString = number.substring(0, 4) + "." + number.substring(4, 7) + "b";
        }
        if (negative) {
            numberString = "-" + numberString;
        }
        return numberString;
    }

    /**
     * every characte after a space or number will be capitalized. This does NOT however make trailing
     * characters lower case, so {@code tEST} will become {@code TEST} and NOT {@code Test}
     *
     * @param s
     * @return T E S T E R
     */
    public static String formatText(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (i == 0) {
                s = String.format("%s%s", Character.toUpperCase(s.charAt(0)),
                    s.substring(1));
            }
            if (!Character.isLetterOrDigit(s.charAt(i))) {
                if (i + 1 < s.length()) {
                    s = String.format("%s%s%s", s.subSequence(0, i + 1),
                        Character.toUpperCase(s.charAt(i + 1)),
                        s.substring(i + 2));
                }
            }
        }
        return s.replace("_", " ");
    }

    public static String formatTextUnderscore(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (i == 0) {
                s = String.format("%s%s", Character.toUpperCase(s.charAt(0)),
                    s.substring(1));
            }
            if (!Character.isLetterOrDigit(s.charAt(i))) {
                if (i + 1 < s.length()) {
                    s = String.format("%s%s%s", s.subSequence(0, i + 1),
                        Character.toUpperCase(s.charAt(i + 1)),
                        s.substring(i + 2));
                }
            }
        }
        return s.replace(" ", "_");
    }

    public static String optimizeText(String text) {
        for (int index = 0; index < text.length(); index++) {
            if (index == 0) {
                text = String.format("%s%s", Character.toUpperCase(text.charAt(0)), text.substring(1));
            }
            if (!Character.isLetterOrDigit(text.charAt(index))) {
                if (index + 1 < text.length()) {
                    text = String.format("%s%s%s", text.subSequence(0, index + 1), Character.toUpperCase(text.charAt(index + 1)), text.substring(index + 2));
                }
            }
        }
        return text;
    }

    /**
     * Capitalizes any characters succeeding a space otherwise we don't worry about any casing.
     *
     * @param string
     * @return
     */
    public static String capitalizeIf(String string) {
        byte[] data = string.getBytes();
        for (int i = 0; i < string.length(); i++) {
            byte b = data[i];
            if (b == 95 || b == 32) {
                if (b == 95)
                    data[i] = 32;
                byte next = data[i + 1];
                if (next >= 97 && next <= 122) {
                    data[i + 1] = (byte) (next - 32);
                }
            }
        }
        if (data[0] >= 97 && data[0] <= 122)
            data[0] = (byte) (data[0] - 32);
        return new String(data, 0, string.length());
    }

    public static String getTotalAmount(int j) {
        if (j >= 10000 && j < 1000000) {
            return j / 1000 + "K";
        } else if (j >= 1000000 && j <= Integer.MAX_VALUE) {
            return j / 1000000 + "M";
        } else {
            return "" + j;
        }
    }

    public static String formatPlayerName(String str) {
        String str1 = Utils.ucFirst(str);
        return str1.replace("_", " ");
    }

    public static String insertCommasToNumber(String number) {
        return number.length() < 4 ? number : insertCommasToNumber(number
            .substring(0, number.length() - 3))
            + ","
            + number.substring(number.length() - 3, number.length());
    }

    private static char decodeBuf[] = new char[4096];

    public static String textUnpack(byte packedData[], int size) {
        int idx = 0, highNibble = -1;
        for (int i = 0; i < size * 2; i++) {
            int val = packedData[i / 2] >> (4 - 4 * (i % 2)) & 0xf;
            if (highNibble == -1) {
                if (val < 13)
                    decodeBuf[idx++] = xlateTable[val];
                else
                    highNibble = val;
            } else {
                decodeBuf[idx++] = xlateTable[((highNibble << 4) + val) - 195];
                highNibble = -1;
            }
        }

        return new String(decodeBuf, 0, idx);
    }

    public static byte[] encode(String string, Buffer buffer) {
        if (string.length() > 80)
            string = string.substring(0, 80);
        string = string.toLowerCase();
        int next = -1;
        for (int index = 0; index < string.length(); index++) {
            char character = string.charAt(index);
            int charIndex = 0;
            for (int count = 0; count < xlateTable.length; count++) {
                if (character != xlateTable[count])
                    continue;
                charIndex = count;
                break;
            }

            if (charIndex > 12) {
                charIndex += 195;
            }
            if (next == -1) {
                if (charIndex < 13)
                    next = charIndex;
                else
                    buffer.writeByte(charIndex);
            } else if (charIndex < 13) {
                buffer.writeByte((next << 4) + charIndex);
                next = -1;
            } else {
                buffer.writeByte((next << 4) + (charIndex >> 4));
                next = charIndex & 0xf;
            }
        }
        if (next != -1) {
            buffer.writeByte(next << 4);
        }

        byte[] buf = new byte[buffer.currentPosition];
        System.arraycopy(buffer.payload, 0, buf, 0, buffer.currentPosition);
        return buf;
    }

    public static final char xlateTable[] = {' ', 'e', 't', 'a', 'o', 'i', 'h', 'n',
        's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b',
        'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6',
        '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-',
        '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"',
        '[', ']'};

    public static String getAOrAn(String s) {
        s = s.toLowerCase();
        if (s.equalsIgnoreCase("anchovies") || s.equalsIgnoreCase("soft clay") || s.equalsIgnoreCase("cheese") || s.equalsIgnoreCase("ball of wool") || s.equalsIgnoreCase("spice") || s.equalsIgnoreCase("steel nails") || s.equalsIgnoreCase("snape grass") || s.equalsIgnoreCase("coal"))
            return "some";
        if (s.startsWith("a") || s.startsWith("e") || s.startsWith("i") || s.startsWith("o") || s.startsWith("u"))
            return "an";
        return "a";
    }

    @SuppressWarnings("rawtypes")
    public static Class[] getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    @SuppressWarnings("rawtypes")
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

    public static String removeSpaces(String s) {
        return s.replace(" ", "");
    }

    public static int getMinutesElapsed(int minute, int hour, int day, int year) {
        Calendar i = Calendar.getInstance();

        if (i.get(1) == year) {
            if (i.get(6) == day) {
                if (hour == i.get(11)) {
                    return i.get(12) - minute;
                }
                return (i.get(11) - hour) * 60 + (59 - i.get(12));
            }

            int ela = (i.get(6) - day) * 24 * 60 * 60;
            return ela > 2147483647 ? 2147483647 : ela;
        }

        int ela = getElapsed(day, year) * 24 * 60 * 60;

        return ela > 2147483647 ? 2147483647 : ela;
    }

    public static int getDayOfYear() {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int days = 0;
        int[] daysOfTheMonth = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if ((year % 4 == 0) && (year % 100 != 0) || (year % 400 == 0)) {
            daysOfTheMonth[1] = 29;
        }
        days += c.get(Calendar.DAY_OF_MONTH);
        for (int i = 0; i < daysOfTheMonth.length; i++) {
            if (i < month) {
                days += daysOfTheMonth[i];
            }
        }
        return days;
    }

    public static int getYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    public static int getElapsed(int day, int year) {
        if (year < 2013) {
            return 0;
        }

        int elapsed = 0;
        int currentYear = Utils.getYear();
        int currentDay = Utils.getDayOfYear();

        if (currentYear == year) {
            elapsed = currentDay - day;
        } else {
            elapsed = currentDay;

            for (int i = 1; i < 5; i++) {
                if (currentYear - i == year) {
                    elapsed += 365 - day;
                    break;
                } else {
                    elapsed += 365;
                }
            }
        }

        return elapsed;
    }

    public static boolean isWeekend() {
        int day = Calendar.getInstance().get(7);
        return (day == 1) || (day == 6) || (day == 7);
    }


    public static byte[] readFile(File s) {
        try {
            FileInputStream fis = new FileInputStream(s);
            FileChannel fc = fis.getChannel();
            ByteBuffer buf = ByteBuffer.allocate((int) fc.size());
            fc.read(buf);
            buf.flip();
            fis.close();
            return buf.array();
        } catch (Exception e) {
            logger.error(new ParameterizedMessage("File: {} missing.", s.getName()), e);
            return null;
        }
    }

    public static byte[] getBuffer(File f) throws Exception {
        if (!f.exists())
            return null;
        byte[] buffer = new byte[(int) f.length()];
        DataInputStream dis = new DataInputStream(new FileInputStream(f));
        dis.readFully(buffer);
        dis.close();
        byte[] gzipInputBuffer = new byte[999999];
        int bufferlength = 0;
        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(buffer));
        do {
            if (bufferlength == gzipInputBuffer.length) {
                logger.warn("Error inflating data.\nGZIP buffer overflow.");
                break;
            }
            int readByte = gzip.read(gzipInputBuffer, bufferlength, gzipInputBuffer.length - bufferlength);
            if (readByte == -1)
                break;
            bufferlength += readByte;
        } while (true);
        byte[] inflated = new byte[bufferlength];
        System.arraycopy(gzipInputBuffer, 0, inflated, 0, bufferlength);
        buffer = inflated;
        if (buffer.length < 10)
            return null;
        return buffer;
    }

    public static int getTimeLeft(long start, int timeAmount, TimeUnit timeUnit) {
        start -= timeUnit.toMillis(timeAmount);
        long elapsed = System.currentTimeMillis() - start;
        int toReturn = timeUnit == TimeUnit.SECONDS ? (int) ((elapsed / 1000) % 60) - timeAmount : (int) ((elapsed / 1000) / 60) - timeAmount;
        if (toReturn <= 0)
            toReturn = 1;
        return timeAmount - toReturn;
    }

    /**
     * Converts an array of bytes to an integer.
     *
     * @param data the array of bytes.
     * @return the newly constructed integer.
     */
    public static int hexToInt(byte[] data) {
        int value = 0;
        int n = 1000;
        for (int i = 0; i < data.length; i++) {
            int num = (data[i] & 0xFF) * n;
            value += num;
            if (n > 1) {
                n = n / 1000;
            }
        }
        return value;
    }

    public static Tile delta(Tile a, Tile b) {
        return new Tile(b.getX() - a.getX(), b.getY() - a.getY());
    }

    private static final String[] BLOCKED_WORDS = new String[]{
        "<img", "@cr", "<img=", ":tradereq:", ":duelreq:",
        "<col=", "<shad=", "[Global]", "[global]", "leaked ip's", "leaked ips", "leaked pass",
        "devious", "firepk", "ventrilica", "ventrili",
        "hade5", "hades5", "pvplegacy", "junglepk", "runeinsanit",
        "n3t", "c  0 m", "c 0 m", "C 0  m",
        "c  o  m", "runeinsanity", "runeinsanit", "runeinsan",
        "rune-insanity", "rune-insanit", ".c0", ".c0m",
        "legendsdomain", "createaforum", "vampirez", "-scape",  "os-base",
        "rswebclients", "pvplegacy", "junglepk", ".n3t",
        "n3t", ".tk", ",net", ",runescape", "pvpmaster", "pvpmasters",
        "PvPMasters.", ",org", ",com", ",be", ",nl", ",info", "dspk",
        ".info", ".org", ".tk", ".net", ".com", ".co.uk", ".be", ".nl",
        ".dk", ".co.cz", ".co", ".us", ".biz", ".eu", ".de", ".cc",
        ".i n f o", ".o r g", ".t k", ".n e t", ".c o m", ".c o . u k",
        ".b e", ".n l", ".d k", ".c o . c z", ".c o", ".u s", ".b i z",
        ".e u", ".d e", ".c c", ".(c)om", "kandarin", "o r g", "www",
        "w w w", "w.w.w", "voidrsps", "void-ps", "desolace", "ikov", "soulsplit",
        "soulspawn", "atiloc", "@cr", "@red", "@whi", "@bla", "@blu",
        "@or1", "@yel", "@gre", "@dre", "@pur", "@cya","i k o v", "ik0v", "<img=", "@cr", ":tradereq:", ":duelreq:",
        "<trans", "<str", "<shad=", "hostilityps", "hostilityp", "hostility ps"
        , "0s-base", "os -base", "os - base", "o s b a s e", "o s - b a s e", "spawnpk", "runex"};


    public static boolean blockedWord(String string) {
        for (String s : BLOCKED_WORDS) {
            if (string.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static String filterMessage(String message) {
        for (String auto : BLOCKED_WORDS) {
            if (message.toLowerCase().contains(auto)) {
                StringBuilder replace = new StringBuilder();
                for (int i = 0; i < auto.length(); i++)
                    replace.append("*");
                message = message.replaceAll(auto, replace.toString());
            }
        }
        return message;
    }

    /**
     * Capitalizes the first letter in said string.
     *
     * @param name The string to capitalize.
     * @return The string with the first char capitalized.
     */
    public static String capitalizeFirst(String name) {
        if (name.length() < 1)
            return "";
        StringBuilder builder = new StringBuilder(name.length());
        char first = Character.toUpperCase(name.charAt(0));
        builder.append(first).append(name.toLowerCase().substring(1));
        return builder.toString();
    }

    /**
     * Formats the name by checking if it starts with a vowel.
     *
     * @param name The string to format.
     */
    public static String getVowelFormat(String name) {
        char letter = name.charAt(0);
        boolean vowel = letter == 'a' || letter == 'e' || letter == 'i' || letter == 'o' || letter == 'u';
        String other = vowel ? "an" : "a";
        return other + " " + name;
    }

    /**
     * Checks if a name is valid according to the {@code VALID_PLAYER_CHARACTERS} array.
     *
     * @param name The name to check.
     * @return The name is valid.
     */
    public static boolean isValidName(String name) {
        return formatNameForProtocol(name).matches("[a-z0-9_]+");
    }

    /**
     * Converts a name to a long value.
     *
     * @param string The string to convert to long.
     * @return The long value of the string.
     */
    public static long stringToLong(String string) {
        long l = 0L;
        for (int i = 0; i < string.length() && i < 12; i++) {
            char c = string.charAt(i);
            l *= 37L;
            if (c >= 'A' && c <= 'Z')
                l += (1 + c) - 65;
            else if (c >= 'a' && c <= 'z')
                l += (1 + c) - 97;
            else if (c >= '0' && c <= '9')
                l += (27 + c) - 48;
        }
        while (l % 37L == 0L && l != 0L)
            l /= 37L;
        return l;
    }

    /**
     * Converts a long to a string.
     *
     * @param l The long value to convert to a string.
     * @return The string value.
     */
    public static String longToString(long l) {
        int i = 0;
        char ac[] = new char[12];
        while (l != 0L) {
            long l1 = l;
            l /= 37L;
            ac[11 - i++] = VALID_CHARACTERS[(int) (l1 - l * 37L)];
        }
        return new String(ac, 12 - i, i);
    }

    public static byte[] getBuffer(String file) {
        try {
            java.io.File f = new java.io.File(file);
            if (!f.exists())
                return null;
            byte[] buffer = new byte[(int) f.length()];
            java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.FileInputStream(f));
            dis.readFully(buffer);
            dis.close();
            return buffer;
        } catch (Exception e) {
            logger.catching(e);
        }
        return null;
    }

    /**
     * Formats a name for use in the protocol.
     *
     * @param name The name to format.
     * @return The formatted name.
     */
    public static String formatNameForProtocol(String name) {
        return name.toLowerCase().replace(" ", "_");
    }

    /**
     * Formats a name for in-game display.
     *
     * @param name The name to format.
     * @return The formatted name.
     */
    public static String formatName(String name) {
        return fixName(name.replace(" ", "_"));
    }

    /**
     * Formats a player's name, i.e sets upper case letters after a space.
     *
     * @param name The name to format.
     * @return The formatted name.
     */
    private static String fixName(String name) {
        if (name.length() > 0) {
            final char ac[] = name.toCharArray();
            for (int j = 0; j < ac.length; j++)
                if (ac[j] == '_') {
                    ac[j] = ' ';
                    if ((j + 1 < ac.length) && (ac[j + 1] >= 'a')
                        && (ac[j + 1] <= 'z')) {
                        ac[j + 1] = (char) ((ac[j + 1] + 65) - 97);
                    }
                }

            if ((ac[0] >= 'a') && (ac[0] <= 'z')) {
                ac[0] = (char) ((ac[0] + 65) - 97);
            }
            return new String(ac);
        } else {
            return name;
        }
    }

    public static final char[] VALID_PIN_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * An array containing valid player name characters.
     */
    public static final char[] VALID_PLAYER_CHARACTERS = {'_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '[', ']', '/', '-', ' '};

    /**
     * An array containing valid characters that may be used on the server.
     */
    public static final char[] VALID_CHARACTERS = {'_', 'a', 'b', 'c', 'd',
        'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
        'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3',
        '4', '5', '6', '7', '8', '9', '!', '@', '#', '$', '%', '^', '&',
        '*', '(', ')', '-', '+', '=', ':', ';', '.', '>', '<', ',', '"',
        '[', ']', '|', '?', '/', '`'
    };

    /**
     * An array containing invalid title characters.
     */
    public static final char[] INVALID_TITLE_CHARACTERS = {'_', '!', '@', '$', '%', '^', '&', '*', '(', ')', '-',
        '+', '=', ':', ';', '.', '>', '<', ',', '"', '[', ']', '|', '?', '/', '`'};

    private static final List<String> MESSAGE_FORMATTING_TAGS = List.of(
        ".com", ".net", ".org", "<img",
        "<sprite", "@cr", "<img=",
        "<col=", "<shad="
    );

    public static boolean containsMessageFormattingTag(String s) {
        for (String tag : MESSAGE_FORMATTING_TAGS) {
            if (s.contains(tag)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasInvalidChars(String title) {
        for (char c : INVALID_TITLE_CHARACTERS) {
            if (title.indexOf(c) != -1) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasInvalidPinChars(String title) {
        for (char c : VALID_PIN_CHARACTERS) {
            if (title.indexOf(c) != -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the formatted time played.
     *
     * @return The time played formatted as a string.
     */
    public static String getFormattedPlayTime(Player player) {
        long different = System.currentTimeMillis() - player.getCreationDate().getTime();


        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

     /*   long days = (long) elapsedJoinDate / 86400; // 86,400
        long daysRemainder = (long) elapsedJoinDate - (days * 86400);
        long hours = (long) daysRemainder / 3600; // 3,600
        long hoursRemainder = (long) daysRemainder - (hours * 3600);
        long minutes = (long) hoursRemainder / 60; // 60
        long seconds = (long) hoursRemainder - (minutes * 60); // remainder*/

        return elapsedDays + "d " + elapsedHours + "h " + elapsedMinutes + "m " + elapsedSeconds + "s";

    }

    public static final int random(int maxValue) {
        if (maxValue == 0)
            return 0;
        return RANDOM_GEN.nextInt(maxValue);
    }

    public static int rand(int range) {
        return (int) (randomFloat() * (range + 1));
    }

    public static boolean roll(int chance) {
        return RANDOM_GEN.nextInt(chance) == 0;
    }

    /**
     * Generates an inclusive pseudo-random number with (approximately) equal
     * probability.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The random generated number.
     */
    public static final int random(final int min, final int max) {
        final int n = Math.abs(max - min);
        return Math.min(min, max) + (n == 0 ? 0 : getRandom(n + 1));
    }

    /**
     * Generates inclusive pseudo-random unique numbers with (approximately) equal
     * probability.
     * This returns an array, since a single number will always be unique,
     * there is no need to use this method for a single number.
     *
     * @param min The minimum value.
     * @param max The maximum value.
     * @return The random generated number.
     */
    public static final int[] uniqueRandoms(final int min, final int max, final int limit) {
        if (limit > max || limit < min) {
            logger.error("Invalid usage of uniqueRandoms method detected.");
            return new int[]{-1};
        }
        return ThreadLocalRandom.current().ints(min, max + 1).distinct().limit(limit).toArray();
    }

    public static double roundOneDecimal(double number_to_format) {
        DecimalFormat decimal_format = new DecimalFormat("#.#");
        return Double.parseDouble(decimal_format.format(number_to_format).replace(",", "."));
    }

    public static double round(double number_to_format) {
        DecimalFormat decimal_format = new DecimalFormat("#.##");
        return Double.parseDouble(decimal_format.format(number_to_format).replace(",", "."));
    }

    public static double get() {
        return ThreadLocalRandom.current().nextDouble();
    }

    public static int get(int maxRange) {
        return (int) (get() * (maxRange + 1D));
    }

    public static int get(int minRange, int maxRange) {
        return minRange + get(maxRange - minRange);
    }

    public static long getLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    public static boolean rollDie(int maxRoll) {
        return rollDie(maxRoll, 1);
    }

    public static boolean rollDie(int sides, int chance) {
        return get(1, sides) <= chance;
    }

    public static boolean rollPercent(int percent) {
        return get() <= (percent * 0.01);
    }

    /**
     * Converts an integer into words.
     */
    public static String convertWord(int amount) {
        return Words.getInstance(amount).getNumberInWords();
    }

    public static String capitalizeJustFirst(String str) {
        str = str.toLowerCase();
        if (str.length() > 1) {
            str = str.substring(0, 1).toUpperCase() + str.substring(1);
        } else {
            return str.toUpperCase();
        }
        return str;
    }

    /**
     * Formats name of enum.
     */
    public static String formatEnum(final String string) {
        return capitalizeSentence(string.toLowerCase().replace("_", " "));
    }

    /**
     * Capitalize each letter after .
     */
    public static String capitalizeSentence(final String string) {
        int pos = 0;
        boolean capitalize = true;
        StringBuilder sb = new StringBuilder(string);
        while (pos < sb.length()) {
            if (sb.charAt(pos) == '.') {
                capitalize = true;
            } else if (capitalize && !Character.isWhitespace(sb.charAt(pos))) {
                sb.setCharAt(pos, Character.toUpperCase(sb.charAt(pos)));
                capitalize = false;
            }
            pos++;
        }
        return sb.toString();
    }

    public static int getClosestX(Entity src, Entity target) {
        return getClosestX(src, target.tile());
    }

    public static int getClosestX(Entity src, Tile target) {
        if (src.getSize() == 1)
            return src.getX();
        if (target.getX() < src.getX())
            return src.getX();
        else if (target.getX() >= src.getX() && target.getX() <= src.getX() + src.getSize() - 1)
            return target.getX();
        else
            return src.getX() + src.getSize() - 1;
    }

    public static int getClosestY(Entity src, Entity target) {
        return getClosestY(src, target.tile());
    }

    public static int getClosestY(Entity src, Tile target) {
        if (src.getSize() == 1)
            return src.getY();
        if (target.getY() < src.getY())
            return src.getY();
        else if (target.getY() >= src.getY() && target.getY() <= src.getY() + src.getSize() - 1)
            return target.getY();
        else
            return src.getY() + src.getSize() - 1;
    }

    public static int getEffectiveDistance(Entity src, Entity target) {
        Tile pos = getClosestTile(src, target);
        Tile pos2 = getClosestTile(target, src);
        return getDistance(pos, pos2);
    }

    public static Tile getClosestTile(Entity src, Entity target) {
        return new Tile(getClosestX(src, target), getClosestY(src, target), src.getZ());
    }

    public static Tile getClosestTile(Entity src, Tile target) {
        return new Tile(getClosestX(src, target), getClosestY(src, target), src.getZ());
    }

    public static int getDistance(Tile src, Tile dest) {
        return getDistance(src.getX(), src.getY(), dest.getX(), dest.getY());
    }

    public static int getDistance(Tile src, int destX, int destY) {
        return getDistance(src.getX(), src.getY(), destX, destY);
    }

    public static int getDistance(int x1, int y1, int x2, int y2) {
        int diffX = Math.abs(x1 - x2);
        int diffY = Math.abs(y1 - y2);
        return Math.max(diffX, diffY);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean desc) {
        List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, (o1, o2) -> {
            if (desc)
                return (o2.getValue()).compareTo(o1.getValue());
            return (o1.getValue()).compareTo(o2.getValue());
        });
        Map<K, V> result = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * Determines the indefinite article of {@code thing}.
     *
     * @param thing the thing to determine for.
     * @return the indefinite article.
     */
    private static String determineIndefiniteArticle(String thing) {
        char first = thing.toLowerCase().charAt(0);
        boolean vowel = first == 'a' || first == 'e' || first == 'i' || first == 'o' || first == 'u';
        return vowel ? "an" : "a";
    }

    /**
     * Determines the plural check of {@code thing}.
     *
     * @param thing the thing to determine for.
     * @return the plural check.
     */
    private static String determinePluralCheck(String thing) {
        boolean needsPlural = !thing.endsWith("s") && !thing.endsWith(")");
        return needsPlural ? "s" : "";
    }

    /**
     * Appends the determined plural check to {@code thing}.
     *
     * @param thing the thing to append.
     * @return the {@code thing} after the plural check has been appended.
     */
    public static String appendPluralCheck(String thing) {
        return thing.concat(determinePluralCheck(thing));
    }


    /**
     * Appends the determined indefinite article to {@code thing}.
     *
     * @param thing the thing to append.
     * @return the {@code thing} after the indefinite article has been appended.
     */
    public static String appendIndefiniteArticle(String thing) {
        return determineIndefiniteArticle(thing).concat(" " + thing);
    }

    public static void createFile(String s) throws IOException {
        File file1 = new File(s);
        if (!file1.exists()) {
            //logger.info("Created {} for first time.", file1.getAbsolutePath());
            file1.createNewFile();
        }
    }

    /**
     * Returns the next float ( from 0.0 to 1.0 ).
     *
     * @return the random double value
     */
    public static double randomFloat() {
        return RANDOM.nextFloat();
    }

    /**
     * Returns the manhattan distance between 2 positions.
     *
     * @param pos
     * @param other
     * @return
     */
    public static int getManhattanDistance(Tile pos, Tile other) {
        return getManhattanDistance(pos.x, pos.y, other.x, other.y);
    }

    /**
     * Gets Manhattan distance
     *
     * @param x
     * @param y
     * @param x2
     * @param y2
     * @return
     */
    public static int getManhattanDistance(int x, int y, int x2, int y2) {
        return Math.abs(x - x2) + Math.abs(y - y2);
    }

    /**
     * Gets the projectile speed modifier
     *
     * @param source The source
     * @param target The target
     */
    public static int getSpeedModifier(Tile source, Tile target) {
        //return 46 + (source.distance(target) * 5);
        return (source.distance(target));
    }

    /**
     * Gets the center location.
     *
     * @return The center location.
     */
    public static Tile getCenterLocation(Entity entity) {
        int offset = entity.getSize() >> 1;
        return entity.tile().transform(offset, offset, 0);
    }

    /**
     * Returns if the entity's block is within distance of the other entity's block.
     *
     * @param pos
     * @param other
     * @param size
     * @param otherSize
     * @param distance
     * @return if is within distance
     */
    public static boolean isWithinDiagonalDistance(Tile pos, Tile other, int size, int otherSize, int distance) {

        int e_offset_x = size - 1 + distance;
        int e_offset_y = size - 1 + distance;

        int o_offset_x = otherSize - 1 + distance;
        int o_offset_y = otherSize - 1 + distance; // z doesnt matter

        boolean inside_entity = (other.x <= pos.x + e_offset_x && other.x >= (pos.x - distance)) && (other.y <= pos.y + e_offset_y && other.y >= (pos.y - distance));

        boolean inside_other = (pos.x <= other.x + o_offset_x && pos.x >= (other.x - distance)) && (pos.y <= other.y + o_offset_y && pos.y >= (other.y - distance));

        return inside_entity || inside_other;
    }

    public static Tile[] getInnerBoundaries(Tile tile, int width, int length) {
        int nextSlot = 0;
        Tile[] boundaries = new Tile[width * length];
        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                boundaries[nextSlot++] = tile.transform(x, y, 0);
            }
        }
        return boundaries;
    }

    public static Player makebot() {
        return makebot("bot");
    }

    public static Player makebot(String user) {
        Player player = new Session(null).getPlayer();
        player.setUsername(user).setLongUsername(Utils.stringToLong(user)).setHostAddress("127.0.0.1");
        player.putAttrib(AttributeKey.MAC_ADDRESS, "OMEGALUL");
        player.onLogin();
        return player;
    }

    public static long secsSince(long time) {
        return (System.currentTimeMillis() - time) / 1000;
    }

    public static long msSince(long time) {
        return System.currentTimeMillis() - time;
    }

    public static String getValueWithoutRepresentationK(long amount) {
        StringBuilder bldr = new StringBuilder();
        if (amount < 1_000) {
            bldr.append(amount);
        } else if (amount >= 1_000 && amount < 10_000_000) {
            bldr.append(amount / 1_000).append("K");
        } else if (amount >= 10_000_000) {
            bldr.append(amount / 1_000_000).append("M");
        }
        return bldr.toString();
    }
}
