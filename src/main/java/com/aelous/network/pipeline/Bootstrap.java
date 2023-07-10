package com.aelous.network.pipeline;

import com.aelous.GameBuilder;
import com.aelous.GameServer;
import com.aelous.annotate.Init;
import com.aelous.core.task.TaskManager;
import com.aelous.model.content.areas.wilderness.content.boss_event.WildernessBossEvent;
import com.aelous.model.content.areas.wilderness.content.todays_top_pkers.TopPkers;
import com.aelous.model.entity.combat.method.impl.npcs.godwars.GwdLogic;
import com.aelous.model.entity.events.star.StarEventTask;
import com.aelous.model.items.Item;
import com.aelous.network.security.HostBlacklist;
import com.aelous.utility.Reflection;
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
    }

    public void scanInitMethods() {
        Reflection.getMethodsAnnotatedWith(Init.class).forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                logger.error("Error loading @Init annotated method[{}] inside class[{}]", method, method.getClass(), e);
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}
