package com.aelous.services.database.transactions;

import com.aelous.services.database.VoidDatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * @author Patrick van Elderen | November, 12, 2020, 18:11
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class MutePlayerDatabaseTransaction extends VoidDatabaseTransaction {
    private static final Logger logger = LogManager.getLogger(MutePlayerDatabaseTransaction.class);
    String username;
    Timestamp expiryDate;
    String reason;

    public MutePlayerDatabaseTransaction(String username, Timestamp expiryDate, String reason) {
        this.username = username;
        this.expiryDate = expiryDate;
        this.reason = reason;
    }

    @Override
    public void executeVoid(Connection connection) throws SQLException {
        try (NamedPreparedStatement statement = prepareStatement(connection,"UPDATE users SET is_muted = 1, mute_expires = :mute_expires, mute_reason = :mute_reason WHERE lower(username) = :username")) {
            statement.setString("username", username.toLowerCase());
            statement.setTimestamp("mute_expires", expiryDate);
            statement.setString("mute_reason", reason);
            //logger.info("Executing query: " + statement.toString());
            statement.executeUpdate();
        }
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error muting the player  " + username + ": ");
        logger.catching(cause);
    }
}
