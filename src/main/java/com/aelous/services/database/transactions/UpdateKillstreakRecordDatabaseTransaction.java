package com.aelous.services.database.transactions;

import com.aelous.services.database.VoidDatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateKillstreakRecordDatabaseTransaction extends VoidDatabaseTransaction {
    private static final Logger logger = LogManager.getLogger(UpdateKillstreakRecordDatabaseTransaction.class);
    int killstreak;
    String username;

    public UpdateKillstreakRecordDatabaseTransaction(int killstreak, String username) {
        this.killstreak = killstreak;
        this.username = username;
    }

    @Override
    public void executeVoid(Connection connection) throws SQLException {
        try (NamedPreparedStatement statement = prepareStatement(connection,"UPDATE users SET killstreak = :killstreak WHERE lower(username) = :username")) {
            statement.setInt("killstreak", killstreak);
            statement.setString("username", username.toLowerCase());
            //logger.info("Executing query: " + statement.toString());
            statement.executeUpdate();
        }
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error updating the killstreak column for Player " + username + ": ");
        logger.catching(cause);
    }
}
