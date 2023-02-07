package com.aelous.model.content.bank_pin.inter;


import com.aelous.GameServer;
import com.aelous.GameEngine;
import com.aelous.model.content.bank_pin.BankPinInterface;
import com.aelous.model.content.bank_pin.dialogue.IncorrectBankPinDialogue;
import com.aelous.model.World;
import com.aelous.model.entity.player.Player;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class ConfirmBankPinInterface extends BankPinInterface {

    private final int pinLength;
    private final List<Integer> previousDigits;

    protected ConfirmBankPinInterface(Player player, int pinLength, List<Integer> previousDigits) {
        super(player);
        this.pinLength = pinLength;
        this.previousDigits = previousDigits;
    }

    @Override
    public void onOpen() {
        player.getPacketSender().sendString(14920, "Now, please confirm your desired PIN.");
        player.getPacketSender().sendString(14923, "Bank of Zaryte");
        player.getPacketSender().sendString(14914, "");
        player.getPacketSender().sendString(14915, "");
        player.getPacketSender().sendString(14916, "");
        player.getPacketSender().sendString(15313, "Click the " + PIN_LENGTH.get(0) + " digit.");
        player.getPacketSender().sendString(14913, pinLength + " digits left");
    }

    @Override
    public void displayText() {
        int digitsEntered = entered.size();
        player.getPacketSender().sendString(15313, "Click the " + PIN_LENGTH.get(digitsEntered) + " digit.");
        player.getPacketSender().sendString(14913, (pinLength - digitsEntered) + " digits left");
    }

    @Override
    public boolean onDigitEntered(int digit, List<Integer> allDigits) {
        if (allDigits.size() == previousDigits.size()) {
            for (int index = 0; index < allDigits.size(); index++) {
                int prevDigit = previousDigits.get(index);
                int nextDigit = allDigits.get(index);
                if (prevDigit != nextDigit) {
                    player.getDialogueManager().start(new IncorrectBankPinDialogue(bankPin, () ->
                        player.getBankPinSettings().createPin(pinLength)));
                    return false;
                }
            }
            String pinToHash = bankPin.pinToString(allDigits);
            GameEngine.getInstance().submitLowPriority(() -> {
                String hashedPin = BCrypt.hashpw(pinToHash, BCrypt.gensalt());
                GameEngine.getInstance().addSyncTask(() -> {
                    if (!player.getBankPin().hasPin()) {
                        bankPin.setPinEntered();
                        bankPin.setPinLength(pinLength);
                        bankPin.setHashedPin(hashedPin);
                        bankPin.setRecoveryDays(GameServer.properties().defaultBankPinRecoveryDays);
                        player.message("Your " + pinLength + "-digit PIN has successfully been created.");
                    } else {
                        player.getBankPin().changePin(hashedPin, pinLength);
                    }
                    player.getInterfaceManager().close();
                    World.getWorld().ls.savePlayerAsync(player);
                });
            });
            return false;
        }
        return true;
    }
}
