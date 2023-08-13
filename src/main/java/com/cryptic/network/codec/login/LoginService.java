package com.cryptic.network.codec.login;

import com.cryptic.GameEngine;
import com.cryptic.model.World;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.entity.player.save.PlayerSave;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Bart on 8/1/2015.
 *
 * <p>Handles logging in, logging out... and being logged in?
 */
public class LoginService implements Service {

    private static final Logger logger = LogManager.getLogger(LoginService.class);

    /**
     * The queue of pending login requests, which is concurrent because there's (at least) two
     * threads accessing this at the same time. One (or more) being the decoder thread from Netty,
     * one (or more) being the login service worker.
     */
    private LinkedBlockingQueue<LoginRequest> messages = new LinkedBlockingQueue<>();

    public final Set<String> ONLINE = Sets.newConcurrentHashSet();

    /** The executor which houses the login service workers. */
    public Executor executor;

    @Override
    public void setup() {}

    public void enqueue(LoginRequest message) {
        messages.add(message);
    }

    public LinkedBlockingQueue<LoginRequest> messages() {
        return messages;
    }

    @Override
    public boolean isAlive() {
        return true; // How could this service possibly be dead??
    }

    @Override
    public boolean start() {
        executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 3; i++) executor.execute(new LoginWorker(this));

        return true;
    }

    @Override
    public boolean stop() {
        return false;
    }

    /**
     * blocking operation, runs on current thread
     *
     * @param request
     * @return
     */
    public CompletableFuture<Boolean> savePlayerFile(Player request) {
        final Stopwatch stopwatch = Stopwatch.createUnstarted();
        stopwatch.start();

        try {
            return PlayerSave.saveAsync(request);
        } catch (Throwable t) {
            // If we don't catch any possible errors, the thread could die silently.
            logger.error("There was an error finishing the logout for " + request.getUsername() + ": ", t);
            // Return a failed CompletableFuture to indicate the error.
            return CompletableFuture.failedFuture(t);
        }
    }


    /**
     * runs on lowPrio executor
     *
     * @param player
     * @return
     */
    public void savePlayerAsync(Player player) {
        GameEngine.getInstance().submitLowPriority(() -> savePlayerFile(player));
    }

    public void saveAllAsync() {
        for (Player player : World.getWorld().getPlayers()) {
            if (player == null) continue;
            savePlayerAsync(player);
        }
    }
}
