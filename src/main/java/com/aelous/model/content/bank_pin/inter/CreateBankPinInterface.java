package com.aelous.model.content.bank_pin.inter;

import com.aelous.model.content.bank_pin.BankPinInterface;
import com.aelous.core.task.Task;
import com.aelous.core.task.TaskManager;
import com.aelous.model.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class CreateBankPinInterface extends BankPinInterface {

    private final int pinLength;

    public CreateBankPinInterface(Player player, int pinLength) {
        super(player);
        this.pinLength = pinLength;
    }

    @Override
    public void onOpen() {
        player.getPacketSender().sendString(14920, "Please enter your desired PIN.");
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
        int totalDigits = allDigits.size();
        if (totalDigits == pinLength) {
            final ArrayList<Integer> integers = new ArrayList<>(allDigits);
            player.getMovementQueue().clear();
            player.getInterfaceManager().close();
            player.message("Thank you. Now please confirm the PIN you entered.");
            TaskManager.submit(new Task("BankPinConfirmTask", 1) {
                @Override
                protected void execute() {
                    player.getMovementQueue().clear();
                    bankPin.setPinInterface(new ConfirmBankPinInterface(player, pinLength, integers));
                    bankPin.getPinInterface().open();
                    stop();
                }
            });
        }
        return true;
    }
}
