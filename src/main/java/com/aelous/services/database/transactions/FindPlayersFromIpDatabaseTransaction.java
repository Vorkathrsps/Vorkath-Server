package com.aelous.services.database.transactions;

import com.aelous.services.database.DatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class FindPlayersFromIpDatabaseTransaction extends DatabaseTransaction<List<String>> {
    private static final Logger logger = LogManager.getLogger(FindPlayersFromIpDatabaseTransaction.class);

    private final String hostAddress;

    public FindPlayersFromIpDatabaseTransaction(String hostAddress) {
        this.hostAddress = hostAddress;
    }

    @Override
    public List<String> execute(Connection connection) throws SQLException {
        List<String> missingPlayers = new ArrayList<String>();
        int id = 0;
        //We could use a join with the player_ips table to find players who have ever used this IP, but checking the last login IP and creation IP is probably good enough.
        try (NamedPreparedStatement statement = prepareStatement(connection, "SELECT username FROM users WHERE last_login_ip = :last_login_ip OR creation_ip = :creation_ip")) {
            statement.setString("last_login_ip", hostAddress);
            statement.setString("creation_ip", hostAddress);
            //logger.info("Executing query: " + statement.toString());
            statement.execute();
            while (statement.getResultSet().next()) {
                missingPlayers.add(statement.getResultSet().getString("username"));
            }
        }

        return missingPlayers;
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error with the find players from IP query: ");
        logger.catching(cause);
    }
}
