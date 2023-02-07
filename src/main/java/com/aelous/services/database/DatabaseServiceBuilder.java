package com.aelous.services.database;

import com.aelous.GameConstants;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import javax.sql.DataSource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class DatabaseServiceBuilder {
    private static final int DEFAULT_SLAVES = 3;
    private static final int DEFAULT_MAXIMUM_T_RETRIES = 5;

    private int slaves = DEFAULT_SLAVES;
    private int maximumTransactionRetries = DEFAULT_MAXIMUM_T_RETRIES;
    private DataSource dataSource;
    private ExecutorService executor;

    public DatabaseServiceBuilder maximumTransactionRetries(int maximumTransactionRetries) {
        this.maximumTransactionRetries = maximumTransactionRetries;
        return this;
    }

    public DatabaseServiceBuilder slaves(int slaves) {
        this.slaves = slaves;
        return this;
    }

    public DatabaseServiceBuilder dataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public DatabaseServiceBuilder executor(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    public DatabaseService build() {
        Preconditions.checkArgument(slaves > 0, "Must have at least one db transaction slave");
        Preconditions.checkArgument(maximumTransactionRetries > 0, "Must have at least one retry");
        Preconditions.checkNotNull(dataSource, "Data source must not be null");

        if (executor == null) {
            executor = Executors.newFixedThreadPool(slaves - 1, new ThreadFactoryBuilder()
                .setNameFormat(GameConstants.SERVER_NAME+"DatabaseServiceThread")
                .build());
        }

        return new DatabaseService(
            dataSource,
            slaves,
            maximumTransactionRetries,
            executor
        );
    }
}
