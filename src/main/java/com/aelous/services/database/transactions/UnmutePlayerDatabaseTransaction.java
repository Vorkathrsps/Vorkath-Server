package com.aelous.services.database.transactions;

import com.aelous.services.database.VoidDatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Patrick van Elderen | November, 12, 2020, 18:15
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class UnmutePlayerDatabaseTransaction extends VoidDatabaseTransaction {
    private static final Logger logger = LogManager.getLogger(UnmutePlayerDatabaseTransaction.class);
    String username;

    public UnmutePlayerDatabaseTransaction(String username) {
        this.username = username;
    }

    @Override
    public void executeVoid(Connection connection) throws SQLException {
        try (NamedPreparedStatement statement = prepareStatement(connection,"UPDATE users SET is_muted = 0 WHERE lower(username) = :username")) {
            statement.setString("username", username.toLowerCase());
            //logger.info("Executing query: " + statement.toString());
            statement.executeUpdate();
        }
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error updating the server status.");
        logger.catching(cause);
    }
}
