package com.aelous.services.database.transactions;

import com.aelous.services.database.VoidDatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import com.aelous.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Patrick van Elderen | November, 12, 2020, 18:44
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public final class UpdatePasswordDatabaseTransaction extends VoidDatabaseTransaction {
    private static final Logger logger = LogManager.getLogger(UpdatePasswordDatabaseTransaction.class);
    private final Player player;
    private final String password;

    public UpdatePasswordDatabaseTransaction(Player player, String password) {
        this.player = player;
        this.password = password;
    }

    @Override
    public void executeVoid(Connection connection) throws SQLException {
        try (NamedPreparedStatement statement = prepareStatement(connection,"UPDATE users SET password = :password WHERE lower(username) = :username")) {
            statement.setString(1, password);
            statement.setString(2, player.getUsername().toLowerCase());
            //logger.info("Executing query: " + statement.toString());
            statement.executeUpdate();
        }
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error updating the password for user " + player.getUsername() + ": ");
        logger.catching(cause);
    }
}
