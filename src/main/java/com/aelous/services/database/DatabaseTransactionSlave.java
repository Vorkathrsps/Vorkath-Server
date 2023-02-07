package com.aelous.services.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;

public class DatabaseTransactionSlave implements Runnable {
    private static final Logger log = LogManager.getLogger(DatabaseTransactionSlave.class);

    private final BlockingQueue<DatabaseJob<?>> jobs;
    private final DatabaseTransactionExecutor executor;

    public DatabaseTransactionSlave(BlockingQueue<DatabaseJob<?>> jobs, DatabaseTransactionExecutor executor) {
        this.jobs = jobs;
        this.executor = executor;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                executor.execute(jobs.take());
            } catch (InterruptedException cause) {
                log.error("We were interrupted while waiting for a job, closing transaction worker.", cause);
                break;
            }
        }
    }

}
