package com.aelous.services.database;

import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseTransaction<T> {
    private static final Logger log = LogManager.getLogger(DatabaseTransaction.class);

    public abstract T execute(Connection connection) throws SQLException;

    public void exceptionCaught(Throwable cause) {
        log.error("Exception while executing transaction.", cause);
    }

    public final NamedPreparedStatement prepareStatement(Connection connection, String sql) throws SQLException {
        return NamedPreparedStatement.create(connection, sql);
    }

    private long lastSleep;

    public void setLastSleep(long lastSleep) {
        this.lastSleep = lastSleep;
    }

    public long getLastSleep() {
        return lastSleep;
    }
}
