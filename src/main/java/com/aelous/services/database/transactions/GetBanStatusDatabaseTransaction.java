package com.aelous.services.database.transactions;

import com.aelous.services.database.DatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * @author Patrick van Elderen | November, 12, 2020, 18:08
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public final class GetBanStatusDatabaseTransaction extends DatabaseTransaction<Boolean> {
    private static final Logger logger = LogManager.getLogger(GetBanStatusDatabaseTransaction.class);

    private String username;

    public GetBanStatusDatabaseTransaction(String username) {
        this.username = username;
    }

    @Override
    public Boolean execute(Connection connection) throws SQLException {
        LocalDateTime currentDateTime = LocalDateTime.now();
        Timestamp expiryDate = null;
        String cid = "", mac = "", ip = "";
        try (NamedPreparedStatement statement = prepareStatement(connection, "SELECT ban_expires, last_login_mac, last_login_cid, last_login_ip FROM users WHERE lower(username) = :username")) {
            statement.setString("username", username.toLowerCase());
            statement.execute();
            if (statement.getResultSet().next()) {
                expiryDate = statement.getResultSet().getTimestamp("ban_expires");
                cid = statement.getResultSet().getString("last_login_cid");
                mac = statement.getResultSet().getString("last_login_mac");
                ip = statement.getResultSet().getString("last_login_ip");
            }
        }
        if (expiryDate != null && currentDateTime.isAfter(expiryDate.toLocalDateTime())) {
            try (NamedPreparedStatement statement = prepareStatement(connection, "UPDATE users SET is_banned = 0 WHERE lower(username) = :username")) {
                statement.setString("username", username.toLowerCase());
                statement.execute(); // purge ban
            }
        }
        try (NamedPreparedStatement statement = prepareStatement(connection, "SELECT is_banned from users WHERE lower(username) = :username")) {
            statement.setString("username", username.toLowerCase());
            //logger.info("Executing query: " + statement.toString());
            statement.execute();
            if (statement.getResultSet().next()) {
                if (statement.getResultSet().getInt("is_banned") == 1)
                    return true; // normal ban active
            }
        }
        if (mac != null && mac.length() > 0) {
            try (NamedPreparedStatement statement = prepareStatement(connection, "SELECT * from macid_bans WHERE macid = :mac")) {
                statement.setString("mac", mac);
                statement.execute();
                while (statement.getResultSet().next()) {
                    Timestamp macExpiryDate = statement.getResultSet().getTimestamp("unban_at");
                    if (macExpiryDate != null && !currentDateTime.isAfter(macExpiryDate.toLocalDateTime())) {
                        return true; // macbanned active
                    }
                }
            }
        }
        if (cid != null && cid.length() > 0) {
            try (NamedPreparedStatement statement = prepareStatement(connection, "SELECT * from clientid_bans WHERE clientid = :cid")) {
                statement.setString("cid", cid);
                statement.execute();
                while (statement.getResultSet().next()) {
                    Timestamp cidExpiryDate = statement.getResultSet().getTimestamp("unban_at");
                    if (cidExpiryDate != null && !currentDateTime.isAfter(cidExpiryDate.toLocalDateTime())) {
                        return true; // cid active
                    }
                }
            }
        }
        if (ip != null && ip.length() > 0) {
            try (NamedPreparedStatement statement = prepareStatement(connection, "SELECT * from ip_bans WHERE ip = :ip")) {
                statement.setString("ip", ip);
                statement.execute();
                while (statement.getResultSet().next()) {
                    Timestamp cidExpiryDate = statement.getResultSet().getTimestamp("unban_at");
                    if (cidExpiryDate != null && !currentDateTime.isAfter(cidExpiryDate.toLocalDateTime())) {
                        return true; // ip active
                    }
                }
            }
        }
        return false;
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error with the find missing users query: ");
        logger.catching(cause);
    }
}
