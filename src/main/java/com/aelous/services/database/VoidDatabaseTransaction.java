package com.aelous.services.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class VoidDatabaseTransaction extends DatabaseTransaction<Void> {
    public abstract void executeVoid(Connection connection) throws SQLException;

    @Override
    public final Void execute(Connection connection) throws SQLException {
        executeVoid(connection);
        return null;
    }
}
