package com.aelous.model.content.bank_pin.inter;


import com.aelous.GameEngine;
import com.aelous.model.content.bank_pin.BankPinInterface;
import com.aelous.model.content.bank_pin.dialogue.IncorrectBankPinDialogue;
import com.aelous.model.entity.player.Player;
import org.mindrot.jbcrypt.BCrypt;

import java.util.List;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class EnterBankPinInterface extends BankPinInterface {

    private final Runnable onEnterPin;

    public EnterBankPinInterface(Player player, Runnable onEnterPin) {
        super(player);
        this.onEnterPin = onEnterPin;
    }

    public EnterBankPinInterface(Player player) {
        this(player, null);
    }

    @Override
    public void onOpen() {
        player.getPacketSender().sendString(14920, "Please enter your " + bankPin.getPinLength() + "-digit PIN using the buttons below.");
        player.getPacketSender().sendString(14923, "Bank of Zaryte");
        player.getPacketSender().sendString(14914, "");
        player.getPacketSender().sendString(14915, "");
        player.getPacketSender().sendString(14916, "");
        player.getPacketSender().sendString(15313, "Click the " + PIN_LENGTH.get(0) + " digit.");
        player.getPacketSender().sendString(14913, bankPin.getPinLength() + " digits left");
    }

    @Override
    public void displayText() {
        int digitsEntered = entered.size();
        player.getPacketSender().sendString(15313, "Click the " + PIN_LENGTH.get(digitsEntered) + " digit.");
        player.getPacketSender().sendString(14913, (bankPin.getPinLength() - digitsEntered) + " digits left");
    }

    @Override
    public boolean onDigitEntered(int digit, List<Integer> allDigits) {
        int totalDigits = allDigits.size();
        if (totalDigits == bankPin.getPinLength()) {
            player.getPacketSender().sendString(15313, "Submitting...");
            checkEnteredPin(allDigits);
            return false;
        }
        return true;
    }

    private void checkEnteredPin(List<Integer> allDigits) {
        String hashedPin = bankPin.getHashedPin();
        String checkPin = bankPin.pinToString(allDigits);
        GameEngine.getInstance().submitLowPriority(() -> {
            // Check bank pin on worker thread.
            if (BCrypt.checkpw(checkPin, hashedPin)) {
                // It matches! Let the player know.
                GameEngine.getInstance().addSyncTask(() -> {
                    bankPin.setPinEntered();
                    if (onEnterPin != null) {
                        bankPin.clearPinInterface();
                        onEnterPin.run();
                    } else {
                        player.getInterfaceManager().close();
                    }
                });
            } else {
                // It doesn't match! Let the player know.
                GameEngine.getInstance().addSyncTask(() ->
                    player.getDialogueManager().start(new IncorrectBankPinDialogue(bankPin, () -> bankPin.enterPin(onEnterPin))));
            }
        });
    }
}
