package com.aelous.services.database.transactions;

import com.aelous.services.database.VoidDatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Patrick van Elderen | July, 17, 2021, 16:18
 * @see <a href="https://github.com/PVE95">Github profile</a>
 */
public class UpdatePlayerInfoDatabaseTransaction extends VoidDatabaseTransaction {

    private static final Logger logger = LogManager.getLogger(UpdatePlayerInfoDatabaseTransaction.class);
    int id;
    String ip;
    String mac;
    int playtime;
    String gameMode;

    public UpdatePlayerInfoDatabaseTransaction(int id, String ip, String mac, int playtime, String gameMode) {
        this.id = id;
        this.ip = ip;
        this.mac = mac;
        this.playtime = playtime;
        this.gameMode = gameMode;
    }

    @Override
    public void executeVoid(Connection connection) throws SQLException {
        try (NamedPreparedStatement statement = prepareStatement(connection,"UPDATE users SET last_login_ip=:ip, last_login_mac=:mac, playtime=:playtime, game_mode=:game_mode WHERE id=:id")) {
            statement.setInt("id", id);
            statement.setString("ip", ip);
            statement.setString("mac", mac);
            statement.setInt("playtime", playtime);
            statement.setString("game_mode", gameMode);
            //logger.info("Executing query: " + statement.toString());
            statement.executeUpdate();
        }
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error updating the info column for Player with id: " + id + ": ");
        logger.catching(cause);
    }
}
