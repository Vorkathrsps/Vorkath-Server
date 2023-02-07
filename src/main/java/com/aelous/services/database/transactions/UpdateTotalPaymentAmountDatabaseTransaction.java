package com.aelous.services.database.transactions;

import com.aelous.services.database.VoidDatabaseTransaction;
import com.aelous.services.database.statement.NamedPreparedStatement;
import com.aelous.model.entity.attributes.AttributeKey;
import com.aelous.model.entity.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Patrick van Elderen | November, 12, 2020, 18:46
 * @see <a href="https://www.rune-server.ee/members/Zerikoth/">Rune-Server profile</a>
 */
public final class UpdateTotalPaymentAmountDatabaseTransaction extends VoidDatabaseTransaction {

    private static final Logger logger = LogManager.getLogger(UpdateTotalPaymentAmountDatabaseTransaction.class);

    private final Player player;

    public UpdateTotalPaymentAmountDatabaseTransaction(Player player) {
        this.player = player;
    }

    @Override
    public void executeVoid(Connection connection) throws SQLException {
        double totalPaymentAmount = player.getAttribOr(AttributeKey.TOTAL_PAYMENT_AMOUNT,0D);
        try (NamedPreparedStatement statement2 = prepareStatement(connection, "UPDATE users SET total_payment_amount = :total_payment_amount WHERE lower(username) = :username")) {
            statement2.setDouble("total_payment_amount", totalPaymentAmount);
            statement2.setString("username", player.getUsername().toLowerCase());
            statement2.execute();
        }
    }

    @Override
    public void exceptionCaught(Throwable cause) {
        logger.error("There was an error with the update total paid amount query for player " + player.getUsername() + ": ");
        logger.catching(cause);
    }
}
