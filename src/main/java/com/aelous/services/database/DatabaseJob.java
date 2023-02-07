package com.aelous.services.database;

import java.util.concurrent.CompletableFuture;

public final class DatabaseJob<T> {
    private final DatabaseTransaction<T> transaction;
    private final CompletableFuture<T> future;

    public DatabaseJob(DatabaseTransaction<T> transaction, CompletableFuture<T> future) {
        this.transaction = transaction;
        this.future = future;
    }

    public CompletableFuture<T> getFuture() {
        return future;
    }

    public DatabaseTransaction<T> getTransaction() {
        return transaction;
    }
}
