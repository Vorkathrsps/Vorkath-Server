package com.aelous.services.database.transactions;

import com.aelous.services.database.VoidDatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import com.aelous.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static com.aelous.model.entity.attributes.AttributeKey.MAC_ADDRESS;

public final class InsertPlayerIPDatabaseTransaction extends VoidDatabaseTransaction {
    private static final Logger logger = LogManager.getLogger(InsertPlayerIPDatabaseTransaction.class);

    private final Player player;

    public InsertPlayerIPDatabaseTransaction(Player player) {
        this.player = player;
    }

    @Override
    public void executeVoid(Connection connection) throws SQLException {
        int userId = 0;
        try (NamedPreparedStatement statement = prepareStatement(connection, "SELECT id from users WHERE lower(username) = :username;")) {
            statement.setString("username", player.getUsername().toLowerCase());
            statement.execute();
            if (statement.getResultSet().next()) {
                userId = statement.getResultSet().getInt("id");
            }
        }
        try (NamedPreparedStatement statement = prepareStatement(connection,"INSERT IGNORE INTO player_ips (user_id, ip, time, mac) VALUES (:user_id, :ip, :time, :mac)")) {
            statement.setInt("user_id", userId);
            statement.setString("ip", player.getHostAddress());
            statement.setTimestamp("time", new Timestamp(new Date().getTime()));
            statement.setString("mac", player.getAttribOr(MAC_ADDRESS, "invalid"));
            statement.executeUpdate();
        }
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error inserting the IP " + player.getHostAddress() + " for Player " + player.getUsername() + ": ");
        logger.catching(cause);
    }
}
