package com.aelous;

import com.aelous.network.pipeline.Bootstrap;
import com.aelous.services.database.DatabaseService;
import com.aelous.services.database.DatabaseServiceBuilder;
import com.aelous.cache.definitions.DefinitionRepository;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import com.aelous.model.entity.player.save.PlayerSave;
import com.aelous.services.discord.DiscordBot;
import com.aelous.utility.test.generic.PlayerProfileVerf;
import com.aelous.utility.DiscordWebhook;
import com.aelous.utility.flood.Flooder;
import com.google.common.base.Preconditions;
import io.netty.util.ResourceLeakDetector;
import com.aelous.cache.DataStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;

import static io.netty.util.ResourceLeakDetector.Level.DISABLED;
import static io.netty.util.ResourceLeakDetector.Level.PARANOID;

/**
 * The starting point of Ferox.
 * Starts the game server.
 *
 * @author Professor Oak
 * @author Lare96
 * @author i_pk_pjers_i
 * @author PVE
 */
public class GameServer {

    /**
     * The logger that will print important information.
     */
    private static final Logger logger;

    /**
     * The flag that determines if the server is currently being updated or not.
     */
    private static volatile boolean isUpdating;

    /**
     * The flag that determines if the server is accepting non-staff logins.
     */
    private static volatile boolean staffOnlyLogins = false;

    /**
     * The flooder used to stress-test the server.
     */
    private static final Flooder flooder = new Flooder();

    public static ServerProperties properties() {
        return ServerProperties.current;
    }

    private static DefinitionRepository definitions;

    public static DefinitionRepository definitions() {
        return definitions;
    }

    /**
     * Filestore instance
     */
    private static DataStore fileStore;

    public static DataStore store() {
        return fileStore;
    }

    static {
        Thread.currentThread().setName(""+GameConstants.SERVER_NAME+"InitializationThread");
        System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        logger = LogManager.getLogger(GameServer.class);
        if (properties().enableDiscordLogging) {
            logger.info("Discord logging has been enabled.");
            commandWebHook = new DiscordWebhook(properties().commandWebHookUrl);
            warningWebHook = new DiscordWebhook(properties().warningWebHookUrl);
            chatWebHook = new DiscordWebhook(properties().chatWebHookUrl);
            stakeWebHook = new DiscordWebhook(properties().stakeWebHookUrl);
            tradeWebHook = new DiscordWebhook(properties().tradeWebHookUrl);
            pmWebHook = new DiscordWebhook(properties().pmWebHookUrl);
            npcDropsWebHook = new DiscordWebhook(properties().npcDropsWebHookUrl);
            playerDropsWebHook = new DiscordWebhook(properties().playerDropsWebHookUrl);
            pickupsWebHook = new DiscordWebhook(properties().pickupsWebHookUrl);
            loginWebHook = new DiscordWebhook(properties().loginWebHookUrl);
            logoutWebHook = new DiscordWebhook(properties().logoutWebHookUrl);
            sanctionsWebHook = new DiscordWebhook(properties().sanctionsWebHookUrl);
            shopsWebHook = new DiscordWebhook(properties().shopsWebHookUrl);
            playerDeathsWebHook = new DiscordWebhook(properties().playerDeathsWebHookUrl);
            passwordChangeWebHook = new DiscordWebhook(properties().passwordChangeWebHookUrl);
            tournamentsWebHook = new DiscordWebhook(properties().tournamentsWebHookUrl);
            referralsWebHook = new DiscordWebhook(properties().referralsWebHookUrl);
            achievementsWebHook = new DiscordWebhook(properties().achievementsWebHookUrl);
            tradingPostSalesWebHook = new DiscordWebhook(properties().tradingPostSalesWebHook);
            tradingPostPurchasesWebHook = new DiscordWebhook(properties().tradingPostPurchasesWebHook);
            raidsWebHook = new DiscordWebhook(properties().raidsWebHook);
            starterBoxWebHook = new DiscordWebhook(properties().starterBoxWebHook);
            clanBoxWebHook = new DiscordWebhook(properties().clanBoxWebHook);
            gambleWebHook = new DiscordWebhook(properties().gambleWebHookUrl);
            boxAndTicketsWebHookUrl = new DiscordWebhook(properties().boxAndTicketsWebHookUrl);
            fpkMerkwebHookURL = new DiscordWebhook(properties().fpkMerkwebHookURL);
        }
    }

    /**
     * The default constructor, will throw an
     * {@link UnsupportedOperationException} if instantiated.
     *
     * @throws UnsupportedOperationException if this class is instantiated.
     */
    private GameServer() {
        throw new UnsupportedOperationException("This class cannot be instantiated!");
    }

    /**
     * The server's start time
     */
    public static long startTime;

     /**
     * The server's bound time
     */
    public static long boundTime;

    public static String broadcast = "";

    private static DatabaseService databaseService;

    private static DiscordWebhook commandWebHook;
    private static DiscordWebhook warningWebHook;
    private static DiscordWebhook chatWebHook;
    private static DiscordWebhook stakeWebHook;
    private static DiscordWebhook tradeWebHook;
    private static DiscordWebhook pmWebHook;
    private static DiscordWebhook npcDropsWebHook;
    private static DiscordWebhook playerDropsWebHook;
    private static DiscordWebhook pickupsWebHook;
    private static DiscordWebhook loginWebHook;
    private static DiscordWebhook logoutWebHook;
    private static DiscordWebhook sanctionsWebHook;
    private static DiscordWebhook shopsWebHook;
    private static DiscordWebhook playerDeathsWebHook;
    private static DiscordWebhook passwordChangeWebHook;
    private static DiscordWebhook tournamentsWebHook;
    private static DiscordWebhook referralsWebHook;
    private static DiscordWebhook achievementsWebHook;
    private static DiscordWebhook tradingPostSalesWebHook;
    private static DiscordWebhook tradingPostPurchasesWebHook;
    private static DiscordWebhook raidsWebHook;
    private static DiscordWebhook starterBoxWebHook;
    private static DiscordWebhook clanBoxWebHook;
    private static DiscordWebhook gambleWebHook;
    private static DiscordWebhook boxAndTicketsWebHookUrl;
    private static DiscordWebhook fpkMerkwebHookURL;

    /**
     * The main method that will put the server online.
     */
    public static void main(String[] args) {
        try {
            //This is the time we start loading the server. We have a separate variable for after we have bound the server.
            startTime = System.currentTimeMillis();
            File store = new File(properties().fileStore);
            if (!store.exists()) {
                throw new FileNotFoundException("Cannot load data store from " + store.getAbsolutePath() + ", aborting.");
            }
            fileStore = new DataStore(properties().fileStore);
            logger.info("Loaded filestore @ (./data/filestore) successfully.");
            definitions = new DefinitionRepository();
            ResourceLeakDetector.setLevel(properties().enableLeakDetection ? PARANOID : DISABLED);
            Preconditions.checkState(args.length == 0, "No runtime arguments needed!");
            //We should only verify player profile integrity with SQL disabled,
            //because the DatabaseService does not exist yet during server startup.
            if (!GameServer.properties().enableSql) {
                PlayerProfileVerf.verifyIntegrity();
            }
            logger.info("Initializing the Bootstrap...");
            Bootstrap bootstrap = new Bootstrap(GameServer.properties().gamePort);
            bootstrap.bind();
            initializeDatabase();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Official Java Shutdownhook: triggered. Players in world: "+World.getWorld().getPlayers().size());
                // This is the FINAL THREAD active when application shuts down.
                // you CANNOT USE old threads (like ExecutorServices) or the TaskManager system.
                // here you should directly run code that runs on this single thread. Emergencies only.
                // P.S log4j wont print to console from here on out.
                for (Player player : World.getWorld().getPlayers()) {
                    if (player == null || !player.isRegistered())
                        continue;
                    // program quit but there are still active players.
                    // A save request never got triggered or program got terminated ungraciously.

                    player.requestLogout();

                    // DIRECT SAVE, assuming the lowPrio executor has stopped/wont run any more save reqs
                    try {
                        new PlayerSave.SaveDetails(player).parseDetails();
                        System.out.printf("DIRECT SAVE: %s%n", player);
                    } catch (Throwable e) {
                        System.out.println("EMERGENCY SAVE FAIL Player "+player+" ex: "+e);
                        e.printStackTrace();
                    }
                }
            }));
            boundTime = System.currentTimeMillis();
            logger.info("Loaded "+GameConstants.SERVER_NAME+ " " + ((GameServer.properties().pvpMode) ? "in PVP mode " : "in economy mode ") + "on port " + GameServer.properties().gamePort + " version v" + GameServer.properties().gameVersion + ".");
            logger.info("The Bootstrap has been bound, "+GameConstants.SERVER_NAME+ " is now online (it took {}ms).", boundTime - startTime);
            //DiscordBot.init();
        } catch (Throwable t) {
            logger.fatal("An error occurred while loading "+GameConstants.SERVER_NAME+".", t);
            System.exit(1);
        }
    }

    public static boolean isUpdating() {
        return isUpdating;
    }

    public static void setUpdating(boolean isUpdating) {
        GameServer.isUpdating = isUpdating;
    }

    public static Flooder getFlooder() {
        return flooder;
    }

    public static boolean isLinux() {
        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();
        String classPath = System.getProperty("java.class.path");
        return osNameMatch.contains("linux");
    }

    public static DatabaseService getDatabaseService() {
        return databaseService;
    }

    public static DatabaseService votesDb;

    private static void initializeDatabase() {
        if (GameServer.properties().enableSql) {
            try {
                databaseService = new DatabaseServiceBuilder()
                    .dataSource(DatabaseService.create(ServerProperties.localProperties.db1))
                    .build();
                databaseService.init();
            } catch (Throwable t) {
                logger.fatal("There was an error initializing the SQL database service, are you sure you have SQL configured?");
                logger.error(t);
                System.exit(1);
            }
            try {
                votesDb = new DatabaseServiceBuilder()
                    .dataSource(DatabaseService.create(ServerProperties.localProperties.db2))
                    .build();
                votesDb.init();
            } catch (Throwable t) {
                logger.fatal("There was an error initializing the SQL database service, are you sure you have SQL configured?");
                logger.error(t);
                System.exit(1);
            }
        } else {
            databaseService = new DatabaseService.DisabledDatabaseService();
        }
    }

    public static DiscordWebhook getCommandWebHook() {
        return commandWebHook;
    }

    public static DiscordWebhook getWarningWebHook() {
        return warningWebHook;
    }

    public static DiscordWebhook getChatWebHook() {
        return chatWebHook;
    }

    public static DiscordWebhook getStakeWebHook() {
        return stakeWebHook;
    }

    public static DiscordWebhook getTradeWebHook() {
        return tradeWebHook;
    }

    public static DiscordWebhook getPmWebHook() {
        return pmWebHook;
    }

    public static DiscordWebhook getNpcDropsWebHook() {
        return npcDropsWebHook;
    }

    public static DiscordWebhook getPlayerDropsWebHook() {
        return playerDropsWebHook;
    }

    public static DiscordWebhook getPickupsWebHook() {
        return pickupsWebHook;
    }

    public static DiscordWebhook getLoginWebHook() {
        return loginWebHook;
    }

    public static DiscordWebhook getLogoutWebHook() {
        return logoutWebHook;
    }

    public static DiscordWebhook getSanctionsWebHook() {
        return sanctionsWebHook;
    }

    public static DiscordWebhook getShopsWebHook() {
        return shopsWebHook;
    }

    public static DiscordWebhook getPlayerDeathsWebHook() {
        return playerDeathsWebHook;
    }

    public static DiscordWebhook getPasswordChangeWebHook() {
        return passwordChangeWebHook;
    }

    public static DiscordWebhook getTournamentWebHook() {
        return tournamentsWebHook;
    }

    public static DiscordWebhook getReferralsWebHook() {
        return referralsWebHook;
    }

    public static DiscordWebhook getAchievementsWebHookWebHook() {
        return achievementsWebHook;
    }

    public static DiscordWebhook getTradingPostPurchasesWebHook() {
        return tradingPostPurchasesWebHook;
    }

    public static DiscordWebhook getTradingPostSalesWebHook() {
        return tradingPostSalesWebHook;
    }

    public static DiscordWebhook getRaidsWebHook() {
        return raidsWebHook;
    }

    public static DiscordWebhook getStarterBoxWebHook() {
        return starterBoxWebHook;
    }

    public static DiscordWebhook getClanBoxWebHook() {
        return clanBoxWebHook;
    }

    public static DiscordWebhook getGambleWebHook() {
        return gambleWebHook;
    }

    public static DiscordWebhook getBoxAndTicketsWebHookUrl() {
        return boxAndTicketsWebHookUrl;
    }

    public static DiscordWebhook getFpkMerkwebHookURL() {
        return fpkMerkwebHookURL;
    }

    public static boolean isStaffOnlyLogins() {
        return staffOnlyLogins;
    }

    public static void setStaffOnlyLogins(boolean staffOnlyLogins) {
        GameServer.staffOnlyLogins = staffOnlyLogins;
    }

}
