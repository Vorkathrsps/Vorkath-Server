package com.cryptic.network.pipeline;

import com.cryptic.GameBuilder;
import com.cryptic.GameServer;
import com.cryptic.annotate.Init;
import com.cryptic.cache.definitions.NpcDefinition;
import com.cryptic.core.task.TaskManager;
import com.cryptic.model.World;
import com.cryptic.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.cryptic.model.content.areas.wilderness.content.todays_top_pkers.TopPkers;
import com.cryptic.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.cryptic.model.entity.events.star.StarEventTask;
import com.cryptic.model.entity.npc.pets.PetDefinitions;
import com.cryptic.model.items.Item;
import com.cryptic.network.security.HostBlacklist;
import com.cryptic.utility.Reflection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The bootstrap that will prepare the game, network, and various utilities.
 * This class effectively enables Eldritch to be put online.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class Bootstrap {

    private static final Logger logger = LogManager.getLogger(Bootstrap.class);

    /**
     * The port that the {@link NetworkBuilder} will listen for connections on.
     */
    private final int port;

    /**
     * The network builder that will initialize the core components of the
     * network.
     */
    private final NetworkBuilder networkBuilder = new NetworkBuilder();

    /**
     * The game builder that will initialize the core components of the game.
     */
    private final GameBuilder gameBuilder = new GameBuilder();

    /**
     * Creates a new {@link Bootstrap}.
     *
     * @param port
     *            the port that the network handler will listen on.
     */
    public Bootstrap(int port) {
        this.port = port;
    }

    /**
     * Binds the core of the server together and puts Eldritch online.
     *
     * @throws Exception
     *             if any errors occur while putting the server online.
     */
    public void bind() throws Exception {
        gameBuilder.initialize();
        networkBuilder.initialize(port);
        GwdLogic.onServerStart();
        HostBlacklist.loadBlacklist();
        if (GameServer.properties().enableDidYouKnowMessages) {
            //TaskManager.submit(new DidYouKnowTask());
        }
        if (GameServer.properties().enableWildernessBossEvents && GameServer.properties().pvpMode) {// Events only on PvP.
            WildernessBossEvent.onServerStart();
            TopPkers.SINGLETON.init();
        }
        TaskManager.submit(new StarEventTask());
        Item.onServerStart();

        for (PetDefinitions value : PetDefinitions.values()) {
            var n = World.getWorld().definitions().get(NpcDefinition.class, value.npc);
            if (n != null)
                n.ignoreOccupiedTiles = true;
        }
    }

    public void scanInitMethods() {
        long start = System.currentTimeMillis();
        Reflection.getMethodsAnnotatedWith(Init.class).parallelStream().forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                logger.error("Error loading @Init annotated method[{}] inside class[{}]", method, method.getClass(), e);
                e.printStackTrace();
                System.exit(1);
            }
        });
        long end = System.currentTimeMillis();
        long elapsed = end - start;
        logger.info("Scanning init methods took {}ms", elapsed);
    }
}
