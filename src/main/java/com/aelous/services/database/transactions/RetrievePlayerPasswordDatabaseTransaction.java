package com.aelous.services.database.transactions;

import com.aelous.services.database.DatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Patrick van Elderen | November, 12, 2020, 18:39
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public class RetrievePlayerPasswordDatabaseTransaction extends DatabaseTransaction<String> {
    private static final Logger logger = LogManager.getLogger(RetrievePlayerPasswordDatabaseTransaction.class);
    private final String username;

    public RetrievePlayerPasswordDatabaseTransaction(String username) {
        this.username = username;
    }

    @Override
    public String execute(Connection connection) throws SQLException {
        String password = "";
        try (NamedPreparedStatement statement = prepareStatement(connection, "SELECT password FROM users WHERE lower(username) = :username")) {
            statement.setString("username", username.toLowerCase());
            //logger.info("Executing query: " + statement.toString());
            statement.execute();
            if (statement.getResultSet().next()) {
                password = statement.getResultSet().getString("password");
            }
        }
        return password;

    }
    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error with the retrieve password query for Player " + username + ": ");
        logger.catching(cause);
    }
}
