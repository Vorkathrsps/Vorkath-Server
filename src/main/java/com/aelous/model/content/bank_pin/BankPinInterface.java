package com.aelous.model.content.bank_pin;

import com.aelous.model.entity.player.Player;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author lare96 <http://github.com/lare96>
 */
public abstract class BankPinInterface {

    /**
     * The list of formatted pin lengths.
     */
    protected static final ImmutableList<String> PIN_LENGTH = ImmutableList.of("1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th");

    /**
     * The list of all possible digits.
     */
    private static final ImmutableList<Integer> DIGITS = ImmutableList.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 0);

    /**
     * The starting line of the bank pin digits.
     */
    private static final int STARTING_LINE = 14883;

    /**
     * The starting button id of the bank pin digits.
     */
    private static final int STARTING_BUTTON = 14873;

    protected final Player player;

    protected final BankPin bankPin;

    private final Stopwatch throttler = Stopwatch.createUnstarted();

    private final Map<Integer, Integer> buttonMap = new HashMap<>();

    protected final List<Integer> entered = new ArrayList<>();

    protected BankPinInterface(Player player) {
        this.player = player;
        bankPin = player.getBankPin();
    }

    public abstract boolean onDigitEntered(int digit, List<Integer> allDigits);


    public final void open() {
        scrambleDisplay();
        onOpen();
        player.getInterfaceManager().open(7424);
    }

    public final boolean enterDigit(int button) {
        if (throttler.isRunning() && throttler.elapsed(TimeUnit.MILLISECONDS) < 400) {
            return false;
        }
        if (!buttonMap.isEmpty() &&
            button >= STARTING_BUTTON && button < STARTING_BUTTON + DIGITS.size()) {
            throttler.reset().start();
            int digit = buttonMap.get(button);
            entered.add(digit);
            if (onDigitEntered(digit, entered)) {
                displayText();
                scrambleDisplay();
            }
            return true;
        }
        return false;
    }

    public final void close() {
        bankPin.clearPinInterface();
        onClose();
    }

    public void onClose() {

    }

    public void onOpen() {

    }

    public abstract void displayText();

    private void scrambleDisplay() {
        buttonMap.clear();
        List<Integer> digits = new ArrayList<>(DIGITS);
        Collections.shuffle(digits);
        int index = 0;
        for (int n : digits) {
            player.getPacketSender().sendString(STARTING_LINE + index, String.valueOf(n));
            buttonMap.put(STARTING_BUTTON + index, n);
            index++;
        }
    }
}
