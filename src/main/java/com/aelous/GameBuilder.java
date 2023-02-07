package com.aelous;

import com.aelous.model.content.areas.wilderness.content.activity.WildernessActivityManager;
import com.aelous.model.inter.clan.ClanRepository;

import com.aelous.model.content.skill.impl.crafting.Crafting;
import com.aelous.model.content.skill.impl.fletching.Fletching;
import com.aelous.model.content.skill.impl.slayer.Slayer;
import com.aelous.model.items.tradingpost.TradingPost;
import com.aelous.model.World;
import com.aelous.utility.loaders.loader.impl.*;
import com.aelous.model.entity.player.commands.impl.players.PromoCodeCommand;
import com.aelous.model.map.region.RegionManager;
import com.aelous.network.packet.incoming.interaction.PacketInteractionManager;
import com.aelous.utility.BackgroundLoader;
import com.aelous.utility.PlayerPunishment;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Loads all required necessities and starts processes required
 * for the game to work.
 * 
 * @author Lare96
 */
public class GameBuilder {

    /**
     * The background loader that will load various utilities in the background
     * while the bootstrap is preparing the server.
     */
    private final BackgroundLoader backgroundLoader = new BackgroundLoader();

    /**
     * Initializes this game builder effectively preparing the background
     * startup tasks and game processing.
     *
     * @throws Exception
     *             if any issues occur while starting the network.
     */
    public void initialize() throws Exception {
        //Start background tasks..
        backgroundLoader.init(createBackgroundTasks());

        //Start prioritized tasks...
        RegionManager.init();

        System.gc(); // Some init scripts allocate a ton to parse

        //Start game engine..
        GameEngine.getInstance().start();

        //Make sure the background tasks loaded properly..
        if (!backgroundLoader.awaitCompletion())
            throw new IllegalStateException("Background load did not complete normally!");
    }

    /**
     * Returns a queue containing all of the background tasks that will be
     * executed by the background loader. Please note that the loader may use
     * multiple threads to load the utilities concurrently, so utilities that
     * depend on each other <b>must</b> be executed in the same task to ensure
     * thread safety.
     *
     * @return the queue of background tasks.
     */
    public Queue<Runnable> createBackgroundTasks() {
        Queue<Runnable> tasks = new ArrayDeque<>();
        tasks.add(ClanRepository::load);
        tasks.add(PromoCodeCommand::init);
        tasks.add(PlayerPunishment::init);
        tasks.add(PacketInteractionManager::init);

        if (GameServer.properties().enableWildernessActivities && GameServer.properties().pvpMode) {
            tasks.add(WildernessActivityManager.getSingleton()::init);
        }

        //Load definitions..
        tasks.add(new BloodMoneyPriceLoader());
        tasks.add(TradingPost::init);
        tasks.add(new Slayer()::loadMasters);
        tasks.add(Crafting::load);
        tasks.add(Fletching::load);
        tasks.add(new ShopLoader());
        tasks.add(new ObjectSpawnDefinitionLoader());
        if(GameServer.properties().pvpMode) {
            tasks.add(new PresetLoader());
        }
        tasks.add(new DoorDefinitionLoader());
        tasks.add(World.getWorld()::postLoad);
        //tasks.add(DiscordBot::init);
        return tasks;
    }
}
