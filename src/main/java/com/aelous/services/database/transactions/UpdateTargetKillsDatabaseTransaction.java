package com.aelous.services.database.transactions;

import com.aelous.services.database.VoidDatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class UpdateTargetKillsDatabaseTransaction extends VoidDatabaseTransaction {
    private static final Logger logger = LogManager.getLogger(UpdateTargetKillsDatabaseTransaction.class);
    int targetKills;
    String username;

    public UpdateTargetKillsDatabaseTransaction(int targetKills, String username) {
        this.targetKills = targetKills;
        this.username = username;
    }

    @Override
    public void executeVoid(Connection connection) throws SQLException {
        try (NamedPreparedStatement statement = prepareStatement(connection,"UPDATE users SET target_kills = :target_kills WHERE lower(username) = :username")) {
            statement.setInt("target_kills", targetKills);
            statement.setString("username", username.toLowerCase());
            //logger.info("Executing query: " + statement.toString());
            statement.executeUpdate();
        }
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error updating the target_kills column for Player " + username + ": ");
        logger.catching(cause);
    }
}
