package com.cryptic;

import com.cryptic.model.entity.player.save.PlayerSaves;
import com.cryptic.network.pipeline.Bootstrap;
import com.cryptic.services.database.DatabaseService;
import com.cryptic.services.database.DatabaseServiceBuilder;
import com.cryptic.cache.definitions.DefinitionRepository;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.save.PlayerSave;
import com.cryptic.tools.CacheTools;
import com.cryptic.utility.test.generic.PlayerProfileVerf;
import com.cryptic.utility.DiscordWebhook;
import com.cryptic.utility.flood.Flooder;
import dev.openrune.cache.CacheManager;
import io.netty.util.ResourceLeakDetector;
import com.cryptic.cache.DataStore;
import lombok.Getter;
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
    @Getter
    private static volatile boolean staffOnlyLogins = false;

    /**
     * The flooder used to stress-test the server.
     */
    @Getter
    private static final Flooder flooder = new Flooder();

    public static ServerProperties properties() {
        return ServerProperties.current;
    }

    public static ServerSettings settings() {
        return ServerSettingsManager.INSTANCE.getSettings();
    }

    public static DefinitionRepository definitions;

    public static DefinitionRepository definitions() {
        return definitions;
    }

    /**
     * Filestore instance
     */
    public static DataStore fileStore;

    public static DataStore store() {
        return fileStore;
    }

    static {
        Thread.currentThread().setName(""+GameServer.settings().getName()+"InitializationThread");
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
            dupeDetectionWebHook = new DiscordWebhook(properties().dupeDetectionUrl);
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

    @Getter
    public static DatabaseService databaseService;

    @Getter
    public static DiscordWebhook commandWebHook;
    @Getter
    public static DiscordWebhook warningWebHook;
    @Getter
    private static DiscordWebhook chatWebHook;
    @Getter
    private static DiscordWebhook stakeWebHook;
    @Getter
    private static DiscordWebhook tradeWebHook;
    @Getter
    private static DiscordWebhook pmWebHook;
    @Getter
    private static DiscordWebhook npcDropsWebHook;
    @Getter
    private static DiscordWebhook playerDropsWebHook;
    @Getter
    private static DiscordWebhook pickupsWebHook;
    @Getter private static DiscordWebhook dupeDetectionWebHook;
    @Getter
    private static DiscordWebhook loginWebHook;
    @Getter
    private static DiscordWebhook logoutWebHook;
    @Getter
    private static DiscordWebhook sanctionsWebHook;
    @Getter
    private static DiscordWebhook shopsWebHook;
    @Getter
    private static DiscordWebhook playerDeathsWebHook;
    @Getter
    private static DiscordWebhook passwordChangeWebHook;
    private static DiscordWebhook tournamentsWebHook;
    @Getter
    private static DiscordWebhook referralsWebHook;
    private static DiscordWebhook achievementsWebHook;
    @Getter
    private static DiscordWebhook tradingPostSalesWebHook;
    @Getter
    private static DiscordWebhook tradingPostPurchasesWebHook;
    @Getter
    private static DiscordWebhook raidsWebHook;
    @Getter
    private static DiscordWebhook starterBoxWebHook;
    @Getter
    private static DiscordWebhook clanBoxWebHook;
    @Getter
    private static DiscordWebhook gambleWebHook;
    @Getter
    private static DiscordWebhook boxAndTicketsWebHookUrl;
    @Getter
    private static DiscordWebhook fpkMerkwebHookURL;

    /**
     * The main method that will put the server online.
     */
    public static void main(String[] args) {
        try {
            startTime = System.currentTimeMillis();
            ServerSettingsManager.INSTANCE.init();
            File store = new File(settings().getCacheLocation());
            if (!store.exists()) throw new FileNotFoundException("Cannot load data store from " + store.getAbsolutePath() + " aborting.");
            fileStore = new DataStore(settings().getCacheLocation());
            logger.info("Loaded filestore {} successfully.", settings().getCacheLocation());
            definitions = new DefinitionRepository();
            CacheManager.INSTANCE.init(store.toPath(), 221);
            ResourceLeakDetector.setLevel(properties().enableLeakDetection ? PARANOID : DISABLED);
            if (!GameServer.properties().enableSql) {
                PlayerProfileVerf.verifyIntegrity();
            }
            logger.info("Initializing the Bootstrap...");
            Bootstrap bootstrap = new Bootstrap(GameServer.properties().gamePort);
            bootstrap.scanInitMethods();
            GameEngine.getInstance().start();
            bootstrap.bind();
            initializeDatabase();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                PlayerSaves.processSaves();
                for (Player player : World.getWorld().getPlayers()) {
                    if (player == null || !player.isRegistered()) continue;
                    player.requestLogout();
                    try {
                        new PlayerSave.SaveDetails(player).parseDetails();
                        System.out.printf("DIRECT SAVE: %s%n", player);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }));
            PlayerSaves.start();
            boundTime = System.currentTimeMillis();
            logger.info("Loaded {} {}on port {} version v{}.", GameServer.settings().getName(), (GameServer.properties().pvpMode) ? "in PVP mode " : "in economy mode ", GameServer.properties().gamePort, GameServer.properties().gameVersion);
            logger.info("The Bootstrap has been bound, {} is now online (it took {}ms).", GameServer.settings().getName(), boundTime - startTime);
        } catch (Throwable t) {
            logger.fatal("An error occurred while loading "+GameServer.settings().getName()+".", t);
            System.exit(1);
        }
    }

    public static boolean isUpdating() {
        return isUpdating;
    }

    public static void setUpdating(boolean isUpdating) {
        GameServer.isUpdating = isUpdating;
    }

    public static boolean isLinux() {
        String osName = System.getProperty("os.name");
        String osNameMatch = osName.toLowerCase();
        String classPath = System.getProperty("java.class.path");
        return osNameMatch.contains("linux");
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

    public static DiscordWebhook getTournamentWebHook() {
        return tournamentsWebHook;
    }

    public static DiscordWebhook getAchievementsWebHookWebHook() {
        return achievementsWebHook;
    }

    public static void setStaffOnlyLogins(boolean staffOnlyLogins) {
        GameServer.staffOnlyLogins = staffOnlyLogins;
    }

}
