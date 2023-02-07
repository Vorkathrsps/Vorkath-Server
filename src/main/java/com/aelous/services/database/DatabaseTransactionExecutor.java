package com.aelous.services.database;

import com.aelous.utility.RandomGen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public final class DatabaseTransactionExecutor {
    private static final RandomGen random = new RandomGen();
    private static final Logger log = LogManager.getLogger(DatabaseTransactionExecutor.class);

    private final DataSource dataSource;
    private final int maximumTransactionRetries;

    public DatabaseTransactionExecutor(DataSource dataSource, int maximumTransactionRetries) {
        this.dataSource = dataSource;
        this.maximumTransactionRetries = maximumTransactionRetries;
    }

    public Connection connection() {
        SQLException e = null;

        for (int i = 0; i < 5; i++) {
            try {
                return dataSource.getConnection();
            } catch (SQLException ee) {
                log.error("Error acquiring connection from psql pool", ee);
                e = ee;
            }
        }

        throw new RuntimeException(e);
    }

    public <T> void execute(DatabaseJob<T> job) throws InterruptedException {
        DatabaseTransaction<T> transaction = job.getTransaction();
        CompletableFuture<T> future = job.getFuture();
        Connection connection = connection();

        for (int attempt = 1; attempt <= maximumTransactionRetries; attempt++) {
            try {
                if (connection.isClosed()) {
                    connection = connection();
                }
                connection.clearWarnings();
                connection.setAutoCommit(false);

                try {
                    T result = transaction.execute(connection);
                    connection.commit();
                    future.complete(result); // Complete after committing
                    safeClose(connection);
                    break;
                } catch (Throwable cause) {
                    try {
                        if (!connection.isClosed())
                            connection.rollback();
                    } catch (SQLException rollbackCause) {
                        log.error("Unable to rollback connection.", rollbackCause);
                    }
                    throw cause; // Rethrow so outer catch can gracefully handle the exception
                }
            } catch (Throwable cause) {
                if (attempt == maximumTransactionRetries) {
                    future.completeExceptionally(cause);
                    log.error(String.format("Unable to execute database transaction after %d attempts. Discarding... - tr=%s", maximumTransactionRetries, transaction), cause);
                    safeClose(connection);
                } else {
                    // Absolute max wait time of max retries*5, in seconds.
                    int max = (maximumTransactionRetries * 1_000) * 5;

                    // Base for this attempt is attempt #, in seconds.
                    int base = attempt * 1_000;

                    // The last sleep, if we never slept, then default of 1 second
                    long lastSleep = transaction.getLastSleep();
                    if (lastSleep == 0) lastSleep = 1_000;

                    // Use decorrelated jitter algorithm to add some randomness to our sleeps.
                    long sleep = Math.min(max, random.nextLong(base, lastSleep * 3));
                    transaction.setLastSleep(sleep);

                    // Log it, yo
                    log.error(String.format("Unable to execute database transaction after attempt %d/%d, retrying in %dms - tr=%s", attempt, maximumTransactionRetries, sleep, transaction), cause);
                    safeClose(connection); // why'd you disable close? temp was just testing shit
                    Thread.sleep(sleep);
                }
            }
        }
    }

    private void safeClose(Connection connection) {
        try {
            if (!connection.isClosed())
                connection.close();
        } catch (Exception ignored) {
        }
    }
}
