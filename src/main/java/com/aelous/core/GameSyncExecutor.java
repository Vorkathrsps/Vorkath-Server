package com.aelous.core;

import com.aelous.GameServer;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;

/**
 * A synchronization executor that executes {@link GameSyncTask}s. These have
 * support for both concurrent and sequential synchronization tasks, and are
 * smart enough to determine when each should be used on a task-to-task basis.
 * 
 * @author lare96 <http://github.org/lare96>
 */
public final class GameSyncExecutor {

    /**
     * The executor that will execute the synchronization tasks. This value may
     * or may not be {@code null}.
     */
    private final ExecutorService service;

    /**
     * The synchronizer that ensures that the thread waits until tasks are
     * completed before proceeding. This value may or may not be {@code null}.
     */
    private final Phaser phaser;

    /**
     * Creates a new {@link GameSyncExecutor}. It automatically determines how
     * many threads; if any, are needed for game synchronization.
     */
    public GameSyncExecutor() {
        this.service = GameServer.properties().concurrency ? create(Math.max(4, Runtime.getRuntime().availableProcessors() / 2)) : null;
        this.phaser = GameServer.properties().concurrency  ? new Phaser(1) : null;
    }

    /**
     * Submits {@code syncTask} to be executed as a synchronization task under
     * this executor. This method can and probably will block the calling thread
     * until it completes.
     * 
     * @param syncTask
     *            the synchronization task to execute.
     */
    public void sync(GameSyncTask syncTask) throws Exception {
        if (service == null || phaser == null || !syncTask.isConcurrent()) {
            for (int index : syncTask.getIndices()) {

                if (!syncTask.checkIndex(index))
                    continue;
                syncTask.execute(index);
            }
            return;
        }

        phaser.bulkRegister(syncTask.getIndices().size());
        for (int index : syncTask.getIndices()) {
            if (!syncTask.checkIndex(index))
                continue;
            final int finalIndex = index;
            service.execute(() -> {
                try {
                    try {
                        syncTask.execute(finalIndex);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    phaser.arriveAndDeregister();
                }
            });
        }
        phaser.arriveAndAwaitAdvance();
    }



    /**
     * Creates and configures the update service for this game sync executor.
     * The returned executor is <b>unconfigurable</b> meaning it's configuration
     * can no longer be modified.
     * 
     * @param nThreads
     *            the amount of threads to create this service.
     * @return the newly created and configured service.
     */
    private ExecutorService create(int nThreads) {
        if (nThreads <= 1)
            return null;
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
        executor.setRejectedExecutionHandler(new CallerRunsPolicy());
        executor.setThreadFactory(new CountingThreadFactory("GameSyncThread"));
        return Executors.unconfigurableExecutorService(executor);
    }
}
