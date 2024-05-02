package com.cryptic.utility;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.cryptic.GameServer;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.cryptic.GameConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * The class that allows a series of tasks or services to be executed
 * asynchronously in the background.
 * <p>
 * <p>
 * Please note that a single background loader instance can only be used once.
 * Once the background load finishes awaiting completion, the executor is
 * shutdown and therefore cannot be reused. Subsequent attempts to reuse
 * background loaders after they've been shutdown will throw an
 * {@link java.lang.IllegalStateException}.
 *
 * @author lare96 <http://github.com/lare96>
 */
public final class BackgroundLoader {

    private static final Logger logger = LogManager.getLogger(BackgroundLoader.class);

    /**
     * The executor that will execute the queue of tasks in the
     * background. Do not do this asynchronously or on more than 1 thread, doing so will cause issues and is slower than using 1 thread.
     */
    private final ExecutorService service = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat(""+ GameServer.settings().getName()+"LoaderThread").setDaemon(true).build());

    /**
     * The queue of tasks that will be executed by the executor.
     */
    private final Queue<Runnable> tasks = new ArrayDeque<>();

    /**
     * The flag that determines if this background loader has been shutdown.
     */
    private boolean shutdown;

    /**
     * Starts this background loader by polling the queue of tasks into the
     * executor.
     * <p>
     * <p>
     * Please note that {@code awaitCompletion()} can be called after this in
     * order to block the underlying thread until the tasks are completed.
     *
     * @param backgroundTasks
     *            the collection of tasks to execute in the background.
     * @throws IllegalStateException
     *             if this background loader has been shutdown.
     */
    public void init(Collection<Runnable> backgroundTasks) {
        Preconditions.checkState(!shutdown && !service.isShutdown(), "This background loader has been shutdown!");
        tasks.addAll(backgroundTasks);
        for (Runnable task : tasks) {
            service.execute(task);
        }
        service.shutdown();
    }

    /**
     * Awaits the completion of the execution of the tasks by the executor. This
     * will block the thread for an infinite amount of time or until the tasks
     * are completed. Once the tasks complete, this background loader will be
     * shutdown and cannot be used again.
     * <p>
     * <p>
     * Please note that {@code start()} must be called before this in order to
     * submit the tasks to the executor.
     *
     * @return {@code true} if the tasks complete and this loader is shutdown
     *         normally, {@code false} otherwise.
     * @throws IllegalStateException
     *             if this background loader has been shutdown.
     */
    public boolean awaitCompletion() {
        Preconditions.checkState(!shutdown, "This background loader has been shutdown!");
        try {
            return service.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("The background service loader was interrupted.", e);
            return false;
        } finally {
            shutdown = true;
        }
    }
}
