package com.aelous;

import com.aelous.utility.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.aelous.model.map.position.Tile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class ServerProperties {

    public static final Gson SERIALIZE = new GsonBuilder().setPrettyPrinting().create();

    private static final Logger logger = LogManager.getLogger(ServerProperties.class);

    static final ServerProperties current;
    public static final LocalProperties localProperties;

    static {
        Path filePathLocal = Paths.get("./data/local-properties.json");
        try {
            if (filePathLocal.toFile().exists()) {
                byte[] readBytes = Files.readAllBytes(filePathLocal);
                LocalProperties loaded = SERIALIZE.fromJson(new String(readBytes), LocalProperties.class);
                if (loaded == null) {
                    logger.info("Server properties file (./data/LocalProperties.json) empty, using default settings.");
                    localProperties = new LocalProperties();
                } else {
                    localProperties = loaded;
                    logger.info("Loaded server properties file (./data/LocalProperties.json) successfully. "+ localProperties);
                }
            } else {
                localProperties = new LocalProperties();
                logger.info("Server properties file (./data/LocalProperties.json) was not found, loaded with default settings.");
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
        try {
            Path filePath = Paths.get("./data/properties.json");
            if (filePath.toFile().exists()) {
                byte[] readBytes = Files.readAllBytes(filePath);
                ServerProperties loaded = SERIALIZE.fromJson(new String(readBytes), ServerProperties.class);
                if (loaded == null) {
                    current = new ServerProperties(localProperties.sqlOn, localProperties.discordLoggingOn);
                    logger.info("Server properties file (./data/properties.json) empty, using default settings.");
                } else {
                    current = loaded;
                    // overwrite with local
                    if (filePathLocal.toFile().exists()) {
                        current.enableDiscordLogging = localProperties.discordLoggingOn;
                        current.enableSql = localProperties.sqlOn;
                    }
                    logger.info("Loaded server properties file (./data/properties.json) successfully.");
                }
            } else {
                current = new ServerProperties(localProperties.sqlOn, localProperties.discordLoggingOn);
                logger.info("Server properties file (./data/properties.json) was not found, loaded with default settings.");
            }
            //Since the static initializer is called after the constructor, we can set the game port here if we don't want to override the game port.
            if (!current.overrideGamePort) {
                if (current.pvpMode) {
                    current.gamePort = 43597;
                } else {
                    current.gamePort = 43596;
                }
            }
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    public static class LocalProperties {
        public final String db1, db2;
        public final boolean sqlOn, discordLoggingOn;

        public LocalProperties() {
            db1 = "default";
            db2 = "votes";
            discordLoggingOn = true;
            sqlOn = true;
        }

        @Override
        public String toString() {
            return "LocalProperties{" +
                "db1='" + db1 + '\'' +
                ", db2='" + db2 + '\'' +
                '}';
        }
    }

    private ServerProperties() {
        this(false, false);
    }

    private ServerProperties(boolean sqlOn, boolean discordLoggingOn) {
        // Default property values. If the server properties file exists, these will be ignored.
        gameVersion = "3";
        definitionsDirectory = "./data/def/";
        dumpDefinitionsDirectory = "./data/dump/";
        clippingDirectory = "./data/map/clipping/";
        debugMode = true;
        autoRefreshQuestTab = true;
        enableLeakDetection = false;
        concurrency = (Runtime.getRuntime().availableProcessors() > 1);
        queuedLoopThreshold = 45;
        packetProcessLimit = 25;
        defaultTile = new Tile(Utils.get(3089, 3092), Utils.get(3489, 3493), 0);
        duelTile = new Tile(3369, 3266);
        defaultClanChat = "";
        queueSwitchingRefresh = true;
        rightClickAutocast = false;
        autosaveMinutes = 15;
        afkLogoutMinutes = 30;
        afkLogoutMinutesNewAccounts = 4;
        baseBMValue = 500;
        requireBankPinOnLogin = false;
        defaultBankPinRecoveryDays = 3;
        displayCycleTime = true;
        useInformationCycle = true;
        informationCycleCount = 10;
        gamePort = 43596;
        overrideGamePort = false;
        enableTraceLogging = false;
        production = false;
        test = false;
        displayCycleLag = false;
        enableWildernessActivities = true;
        enableWildernessBossEvents = true;
        refreshQuestTabCycles = 100;
        hitPredictorEnabled = false;
        riskManagementVeryRareRollRange = "100-90";
        riskManagementRareRollRange = "90-75";
        riskManagementUncommonRollRange = "75-45";
        tournamentsEnabled = true;
        enableSql = sqlOn;
        linuxOnlyDisplayCycleLag = false;
        enableStarterTasks = true;
        ignoreGameLagDetectionMilliseconds = 10000;
        clearQuestInterfaceStrings = false;
        warnSlowPackets = false;
        tournamentMaxParticipants = 150;
        tournamentMinProdParticipants = 6;
        tournamentMinDevParticipants = 2;
        logSuccessfulPackets = false;
        logUnderflowPacketsProduction = false;
        enableNpcDropListInterface = true;
        connectionLimit = 4;
        maxAlts = 2;
        pidIntervalTicks = 100;
        enablePidShuffling = true;
        skullTime = 720;
        enableItemSpritesOnNpcDropInterface = true;
        linuxOnlyDisplayCycleTime = false;
        linuxOnlyDisplayTaskLag = false;
        BCryptPasswordRounds = 12;
        BCryptPinRounds = 10;
        logAccuracyChances = false;
        soundsEnabled = false;
        enableDueling = true;
        enableGambling = true;
        playerKillFillsSpec = false;
        maintenanceMode = false;
        enableDidYouKnowMessages = true;
        enableLoadLastDuelPreset = true;
        enablePasswordChangeLogging = true;
        enableMoneyPouch = false;
        venomVsPlayersOn = false;
        venomFromAdminsOn = false;
        fileStore = "";
        definitionsLazy = false;
        discordNotifyId = "";
        enableDiscordLogging = discordLoggingOn;
        commandWebHookUrl = "";
        warningWebHookUrl = "";
        chatWebHookUrl = "";
        stakeWebHookUrl = "";
        tradeWebHookUrl = "";
        pmWebHookUrl = "";
        npcDropsWebHookUrl = "";
        playerDropsWebHookUrl = "";
        pickupsWebHookUrl = "";
        loginWebHookUrl = "";
        logoutWebHookUrl = "";
        sanctionsWebHookUrl = "";
        shopsWebHookUrl = "";
        playerDeathsWebHookUrl = "";
        passwordChangeWebHookUrl = "";
        tournamentsWebHookUrl = "";
        referralsWebHookUrl = "";
        achievementsWebHookUrl = "";
        tradingPostSalesWebHook = "";
        tradingPostPurchasesWebHook = "";
        raidsWebHook = "";
        starterBoxWebHook = "";
        clanBoxWebHook = "";
        gambleWebHookUrl = "";
        boxAndTicketsWebHookUrl = "";
        fpkMerkwebHookURL = "";
        wildernessDitchEnabled = false;
        redirectOutStream = false;
        teleToMeInWildOk = false;
        teleToWildyPlayedDisabled = false;
        brewCap = 14;
        newAccsBMTime = 3000;
        pkTelesAfterSetupSet = 50;
        jailOres = 167;
        brewDroppingBlocked = true;
        stakingStaffOnly = false;
        edgeDitch10secondPjTimerEnabled = true;
        punishmentsToDatabase = false;
        punishmentsToLocalFile = true;
        enableChangeAccountType = true;
        buyTwoGetOneFree = true;
        promoEnabled = true;
        donatorPromo = true;
        doubleExperienceEvent = true;
        doubleSlayerRewardPointsEvent = false;
        doubleBMEvent = false;
        doubleVotePointsEvent = true;
        pvpMode = true;
        nerfDropRateBoxes = true;
    }

    public final String fileStore;
    public final boolean definitionsLazy;

    /**
     * The current game/client version.
     */
    public final String gameVersion;

    /**
     * The directory of the definition files.
     */
    public final String definitionsDirectory;

    public final String dumpDefinitionsDirectory;

    /**
     * The directory of the clipping files.
     */
    public final String clippingDirectory;

    /**
     * A flag used for determining if admins will see debug statements or not.
     */
    public final boolean debugMode;

    /**
     * A flag used for determining if the quest tab will automatically refresh or not.
     */
    public final boolean autoRefreshQuestTab;

    /**
     * A flag used for how often the quest tab will refresh. Use 1 for every cycle, use 100 for every 100 cycles (every minute)
     */
    public final int refreshQuestTabCycles;

    /**
     * Resource leak detector set to PARANOID (ENABLE_LEAK_DETECTION TRUE) may cause lag (especially if hosting) but checks for any resource leaks. Probably should set ENABLE_LEAK_DETECTION to false if hosting.
     */
    public final boolean enableLeakDetection;

    /**
     * The flag that determines if processing should be parallelized, improving
     * the performance of the server times {@code n} (where
     * {@code n = Runtime.getRuntime().availableProcessors()}) at the cost of
     * substantially more CPU usage.
     */
    public final boolean concurrency;

    /**
     * The maximum amount of iterations for a queue/list that should occur each cycle.
     */
    public final int queuedLoopThreshold;

    /**
     * The maximum amount of messages that can be processed in one sequence.
     * This limit may need to be increased or decreased in the future.
     */
    public final int packetProcessLimit;

    /**
     * The default position, where players will
     * spawn upon logging in for the first time.
     */
    public final Tile defaultTile;

    /**
     * The default position, where players will
     * spawn upon logging in for the first time.
     */
    public final Tile duelTile;

    /**
     * The default clan chat a player will join upon logging in,
     * if they aren't in one already.
     */
    public final String defaultClanChat;

    /**
     * Should the inventory be refreshed immediately
     * on switching items or should it be delayed
     * until next game cycle?
     */
    public final boolean queueSwitchingRefresh;

    public final boolean rightClickAutocast;

    public final int autosaveMinutes;

    // Idle packet called every 3 minutes, so the value should be a multiple of 3 for the most accurate timing.
    public final int afkLogoutMinutes;

    // Idle packet called every 3 minutes, so the value should be a multiple of 3 for the most accurate timing.
    public final int afkLogoutMinutesNewAccounts;

    //Should we display the Game Engine cycle time in the server log?
    public final boolean displayCycleTime;

    /**
     * See ShopManager for details, 10 is probably a good ratio, 20 or higher might be a better ratio.
     */
    public final int baseBMValue;

    /**
     * Do players need to enter their bank PIN (if set) upon login? If so, set to true.
     */
    public final boolean requireBankPinOnLogin;

    /**
     * The default recovery delay for bank PINs.
     */
    public final int defaultBankPinRecoveryDays;
    /**
     * If this is true, we will only print the cycle information once per informationCycleCount.
     */
    public final boolean useInformationCycle;

    /**
     * The count for which tick we will print cycle information if we use the information cycle.
     */
    public final int informationCycleCount;

    /**
     * The game port that the server listens on and the client connects to.
     */
    public int gamePort;

    /**
     * If we want to override the PVP and ECO game ports, set this to true.
     */
    public final boolean overrideGamePort;

    /**
     * If we want to enable trace logging (the most verbose type of logging) in log4j2, set this to true.
     */
    public final boolean enableTraceLogging;

    /**
     * If we are running in production, set this to true.
     */
    public final boolean production;

    /**
     * If we are running in test, set this to true.
     */
    public final boolean test;

    /**
     * If we want to see warnings and errors about game engine cycle time lag, set this to true.
     * This should be false on slow dev PCs and always true on production.
     */
    public final boolean displayCycleLag;

    /**
     * If we want to enable wilderness activities to encourage more people to use the wilderness, set this to true.
     */
    public final boolean enableWildernessActivities;

    /**
     * If we want to enable wilderness boss events to encourage more people to use the wilderness, set this to true.
     */
    public final boolean enableWildernessBossEvents;

    /**
     * If we want to enable the hit predictor for all players, set this to true. Make sure to match the client constant for this to not confuse players.
     */
    public final boolean hitPredictorEnabled;

    /**
     * This is the range that the risk management very rare items will roll. It must be expressed as two numbers with a hyphen.
     */
    public final String riskManagementVeryRareRollRange;

    /**
     * This is the range that the risk management rare items will roll. It must be expressed as two numbers with a hyphen.
     */
    public final String riskManagementRareRollRange;

    /**
     * This is the range that the risk management uncommon items will roll. It must be expressed as two numbers with a hyphen.
     */
    public final String riskManagementUncommonRollRange;

    /**
     * If we want to disable PvP tournaments, set this to false.
     */
    public final boolean tournamentsEnabled;

    /**
     * If we want to disable SQL, set this to false.
     */
    public boolean enableSql;

    /**
     * If we want to only display cycle lag messages on Linux, set this to true.
     */
    public final boolean linuxOnlyDisplayCycleLag;

    /**
     * If we want to enable starter tasks, set this to true.
     */
    public final boolean enableStarterTasks;

    /**
     * This is the value for how many milliseconds we want to wait for calculating game lag.
     * A reasonable value would be 10000ms which is 10 seconds from when the server is started.
     * Set this to 0 to disable this functionality and instead calculate game lag no matter what.
     */
    public final int ignoreGameLagDetectionMilliseconds;

    /**
     * If we want to clear quest interface strings like for commands, set this to true.
     */
    public final boolean clearQuestInterfaceStrings;

    /**
     * If we want to warn for slow packets (packets > 0ms), set this to true.
     */
    public final boolean warnSlowPackets;

    /**
     * This is the number the max number of participants for the PvP tournaments system.
     */
    public final int tournamentMaxParticipants;

    /**
     * This is the number the min number of participants for the PvP tournaments system in production mode.
     */
    public final int tournamentMinProdParticipants;

    /**
     * This is the number the min number of participants for the PvP tournaments system in development mode.
     */
    public final int tournamentMinDevParticipants;

    /**
     * If we want to log successful packets, set this to true. We don't really need this.
     */
    public final boolean logSuccessfulPackets;

    /**
     * If we want to log packet size mismatch on production, set this to true.
     * We probably don't need this set to true, since packet fragmentation should be expected in production.
     */
    public final boolean logUnderflowPacketsProduction;

    /**
     * If we want to enable the NPC drop list interface (uses the same interface as commands list, set this to true.
     */
    public final boolean enableNpcDropListInterface;

    /**
     * a map of ip:count of connections open in netty. not 1:1 with attempting to login because the client
     * might send a js5 or cache request or something else
     */
    public final int connectionLimit;

    /**
     * The amount of connections that are allowed from the same host.
     * This limit may need to be increased or decreased in the future.
     */
    public final int maxAlts;

    /**
     * The number of ticks before PID is shuffled.
     */
    public final int pidIntervalTicks;

    /**
     * If we want to enable PID shuffling, set this to true.
     */
    public final boolean enablePidShuffling;

    /**
     * The default skull timer
     */
    public final int skullTime;

    /**
     * If we want to draw item sprites on the NPC Drop interface, set this to true.
     */
    public final boolean enableItemSpritesOnNpcDropInterface;

    /**
     * If we want to only display cycle time (logging) on Linux, set this to true.
     */
    public final boolean linuxOnlyDisplayCycleTime;

    /**
     * If we want to only display task lag (logging) on Linux, set this to true.
     */
    public final boolean linuxOnlyDisplayTaskLag;

    /**
     * This is the default BCrypt rounds we use for passwords. 12 is probably a good default.
     */
    public final int BCryptPasswordRounds;

    /**
     * This is the default BCrypt rounds we use for bank PINs. 10 is probably a good default.
     */
    public final int BCryptPinRounds;

    /**
     * If we want to enable logging of accuracy chances, set this to true.
     */
    public final boolean logAccuracyChances;

    /**
     * If we want to enable sounds, set this to true.
     */
    public final boolean soundsEnabled;

    /**
     * If we want to enable dueling, set this to true.
     */
    public final boolean enableDueling;

    /**
     * If we want to enable gambling, set this to true.
     */
    public final boolean enableGambling;

    /**
     * If we want player kills to fill spec, set this to true.
     */
    public final boolean playerKillFillsSpec;

    /**
     * If we want to enable maintenance mode at startup, set this to true.
     */
    public final boolean maintenanceMode;

    /**
     * If we want to enable did you know messages, set this to true.
     */
    public final boolean enableDidYouKnowMessages;

    /**
     * If we want to enable did you know messages, set this to true.
     */
    public final boolean enableLoadLastDuelPreset;

    /**
     * If we want to enable password change logging for debugging password randomly changing, set this to true.
     */
    public final boolean enablePasswordChangeLogging;

    /**
     * If we want to enable the money pouch, set this to true.
     */
    public final boolean enableMoneyPouch;

    // Disabled until fully coded
    public final boolean venomVsPlayersOn;

    // During testing phase
    public final boolean venomFromAdminsOn;

    /**
     * This is the Discord ID to notify for any errors.
     */
    public final String discordNotifyId;

    /**
     * If we want logging to our Discord server, set this to true.
     */
    public boolean enableDiscordLogging;

    /**
     * This is the command web hook URL for Discord.
     */
    public final String commandWebHookUrl;
    /**
     * This is the warning web hook URL for Discord.
     */
    public final String warningWebHookUrl;
    /**
     * This is the chat web hook URL for Discord.
     */
    public final String chatWebHookUrl;
    /**
     * This is the trade web hook URL for Discord.
     */
    public final String tradeWebHookUrl;
    /**
     * This is the stake web hook URL for Discord.
     */
    public final String stakeWebHookUrl;
    /**
     * This is the pm web hook URL for Discord.
     */
    public final String pmWebHookUrl;
    /**
     * This is the NPC drops web hook URL for Discord.
     */
    public final String npcDropsWebHookUrl;
    /**
     * This is the player drops web hook URL for Discord.
     */
    public final String playerDropsWebHookUrl;
    /**
     * This is the pickups web hook URL for Discord.
     */
    public final String pickupsWebHookUrl;
    /**
     * This is the login web hook URL for Discord.
     */
    public final String loginWebHookUrl;
    /**
     * This is the logout web hook URL for Discord.
     */
    public final String logoutWebHookUrl;

    /**
     * This is the sanctions web hook URL for Discord.
     */
    public final String sanctionsWebHookUrl;
    /**
     * This is the shops web hook URL for Discord.
     */
    public final String shopsWebHookUrl;
    /**
     * This is the player deaths web hook URL for Discord.
     */
    public final String playerDeathsWebHookUrl;
    /**
     * This is the password change web hook URL for Discord.
     */
    public final String passwordChangeWebHookUrl;
    /**
     * This is the tournaments web hook URL for Discord.
     */
    public final String tournamentsWebHookUrl;
    /**
     * This is the referrals web hook URL for Discord.
     */
    public final String referralsWebHookUrl;
    /**
     * This is the achievements web hook URL for Discord.
     */
    public final String achievementsWebHookUrl;
    /**
     * This is the trading post sales web hook URL for Discord.
     */
    public final String tradingPostSalesWebHook;
    /**
     * This is the trading post purchases web hook URL for Discord.
     */
    public final String tradingPostPurchasesWebHook;
    /**
     * This is the raids web hook URL for Discord.
     */
    public final String raidsWebHook;
    /**
     * This is the starter box web hook URL for Discord.
     */
    public final String starterBoxWebHook;
    /**
     * This is the clan box web hook URL for Discord.
     */
    public final String clanBoxWebHook;
    /**
     * This is the gamble web hook URL for Discord.
     */
    public final String gambleWebHookUrl;
    /**
     * This is the box and tickets web hook URL for Discord.
     */
    public final String boxAndTicketsWebHookUrl;
    /**
     * This is the fpk merk web hook URL for Discord.
     */
    public final String fpkMerkwebHookURL;
    /**
     * Can we walk over the wilderness ditch.
     */
    public final boolean wildernessDitchEnabled;

    public final boolean redirectOutStream;

    // Stops non-admins doing 'teletome' if they're in the wilderness.
    public final boolean teleToMeInWildOk;

    // Every code below here is not by @god_coder and is shit code by @jak
    // Stops non-admins doing 'teleto' if the target is in the wilderness ft. hybrid abuse x2 in 1 hour
    public final boolean teleToWildyPlayedDisabled;

    // maximum brews you can take into the wilderness via teleports. You can still jump the ditch and run up to 30+ but who does that :)
    public final int brewCap;

    // Game ticks before new accounts drop blood money (stops farming)
    public final int newAccsBMTime;

    // How many seconds you have to wait before using teleports (tabs, spellbook, wizard)
    public final int pkTelesAfterSetupSet;

    // How many ores need to be mined before you can escape from the jail.
    public final int jailOres;

    // If food that is dropped in wild should be hidden to others.
    public final boolean brewDroppingBlocked;

    // If duel arena can only be used when you're a mod.
    public final boolean stakingStaffOnly;

    // If the pj timer is 10 seconds instead of 4.5s at edgeville. Stops pjers.
    public final boolean edgeDitch10secondPjTimerEnabled;

    // If this is enabled punishments will be send to the database
    public final boolean punishmentsToDatabase;

    // If this is enabled punishments will be send to a local file
    public final boolean punishmentsToLocalFile;

    /**
     *  If we want to enable players changing between PK mode and trained mode, set this to true.
     */
    public final boolean enableChangeAccountType;

    // If this promo is active when purchasing 2 of the same items from the store you get one for free
    public final boolean buyTwoGetOneFree;

    // If this promo is active every 25€ you get a mystery ticket
    public final boolean donatorPromo;

    // Is the promo interface enabled or disabled
    public final boolean promoEnabled;

    public final boolean doubleExperienceEvent;
    public final boolean doubleSlayerRewardPointsEvent;
    public final boolean doubleBMEvent;
    public final boolean doubleVotePointsEvent;
    public final boolean pvpMode;
    public final boolean nerfDropRateBoxes;

}

