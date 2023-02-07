package com.aelous.services.database;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.aelous.GameEngine;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class DatabaseService {

    private static final Logger log = LogManager.getLogger(DatabaseService.class);

    public static HikariDataSource create(String name) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newBufferedReader(Paths.get(name + ".ini")));
        } catch (IOException cause) {
            throw new UncheckedIOException("Unable to load " + name + ".ini properties. Does it exist?", cause);
        }

        HikariConfig config = new HikariConfig();
        // For some reason, we need to explicitly specify the timezone to prevent a stacktrace of "EDT" unrecognized.
        String url = "jdbc:mysql://" + properties.getProperty("host") + "/" + properties.getProperty("database") + "?serverTimezone=" + properties.getProperty("serverTimezone");
        log.info("Creating mysql database service [{}] with url: {}", name, url);
        config.setJdbcUrl(url);
        config.setAutoCommit(false);
        config.setRegisterMbeans(true);
        config.setPoolName(name);
        config.setMinimumIdle(Integer.parseInt(properties.getProperty("maximumConnections")));
        config.setConnectionTimeout(30_000);
        config.setMaximumPoolSize(512);
        config.setLeakDetectionThreshold(60_000);
        config.setUsername(properties.getProperty("username"));
        config.setPassword(properties.getProperty("password"));
        config.validate();
        return new HikariDataSource(config);
    }

    private final int slaves;
    private final ExecutorService executor;
    private final DatabaseTransactionExecutor transactionExecutor;
    private final ExecutorService backgroundExecutor = Executors.newFixedThreadPool(8, new ThreadFactoryBuilder()
        .setPriority(Thread.NORM_PRIORITY)
        .setNameFormat("ArctusDatabaseServiceBackgroundThread")
        .build()); //Since this is a background pool with very low maximum usage, it's fine to have it at 8 threads since it will be idle 99% of the time. Normal priority so the tasks can be executed ASAP.
    private final BlockingQueue<DatabaseJob<?>> jobs = new LinkedBlockingDeque<>();

    public DatabaseService(DataSource dataSource, int slaves, int maximumTransactionRetries, ExecutorService executor) {
        this.slaves = slaves;
        this.executor = executor;
        this.transactionExecutor = new DatabaseTransactionExecutor(dataSource, maximumTransactionRetries);
    }

    public void init() {
        for (int i = 0; i < slaves; i++) {
            executor.submit(new DatabaseTransactionSlave(jobs, transactionExecutor));
        }
    }

    /**
     * Submits the specified {@link DatabaseTransaction} to be executed at some point in the {@link Future}.
     *
     * @param transaction The DatabaseTransaction we are executing.
     * @param <T>         The expected DatabaseTransaction result type.
     * @return The result of the DatabaseTransaction at some point in the Future.
     */
    private <T> CompletableFuture<T> _submit(DatabaseTransaction<T> transaction) {
        CompletableFuture<T> future = new CompletableFuture<>();
        DatabaseJob<T> job = new DatabaseJob<>(transaction, future);
        jobs.add(job);
        return future;
    }

    public <T> void submit(DatabaseTransaction<T> transaction) {
        _submit(transaction);
    }

    /**
     * Submits the specified {@link DatabaseTransaction} to be executed at some point in the {@link Future}.
     *
     * <p>
     * This method utilizes a low priority background {@link ExecutorService}
     * to wait for {code transaction}s to complete before passing the {@code T} result to the
     * appropriate {@code success} or {@code failure} Consumer
     * </p>
     *
     * @param transaction The DatabaseTransaction we are executing.
     * @param success     A {@link Consumer} which is executed if the {@code transaction} was successful.
     * @param <T>         The expected DatabaseTransaction result type.
     */
    public <T> void submit(DatabaseTransaction<T> transaction, Consumer<T> success) {
        CompletableFuture<T> future = _submit(transaction);
        backgroundExecutor.submit(() -> {

            do { // Wait for future to finish before continuing...
                try {
                    Thread.sleep(50);
                } catch (InterruptedException cause) {
                    future.completeExceptionally(cause);
                }
            } while (!future.isDone());

            GameEngine.getInstance().addSyncTask(() -> {
                try {
                    if (future.isCompletedExceptionally()) {
                        transaction.exceptionCaught((Throwable) future.get());
                    } else {
                        success.accept(future.get());
                    }
                } catch (Throwable cause) {
                    transaction.exceptionCaught(cause);
                }
            });
        });
    }

    /**
     * Executes the specified {@link DatabaseTransaction} immediately. This method is <strong>blocking</strong>!
     *
     * @param transaction The DatabaseTransaction we are executing.
     * @param <T>         The expected DatabaseTransaction result type.
     * @return The result of the DatabaseTransaction.
     * @throws ExecutionException   If this future completed exceptionally.
     * @throws InterruptedException If the current thread was interrupted while waiting.
     */
    public <T> T execute(DatabaseTransaction<T> transaction) throws InterruptedException, ExecutionException {
        return _submit(transaction).get();
    }

    public static class DisabledDatabaseService extends DatabaseService {

        public DisabledDatabaseService() {
            super(null, 0, 0, null);
        }

        @Override
        public void init() {
        }

        @Override
        public <T> void submit(DatabaseTransaction<T> transaction) {
        }

        @Override
        public <T> void submit(DatabaseTransaction<T> transaction, Consumer<T> success) {
        }

        @Override
        public <T> T execute(DatabaseTransaction<T> transaction) {
            return null;
        }
    }
}
