package com.aelous.model.content.bank_pin;

import java.time.LocalDateTime;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class BankPinModification {

    private final String hashedPin;
    private final int pinLength;
    private final int recoveryDays;
    private final LocalDateTime activationDate;
    private final String description;

    public BankPinModification(String hashedPin, int pinLength, int recoveryDays, LocalDateTime activationDate, String description) {
        this.hashedPin = hashedPin;
        this.pinLength = pinLength;
        this.recoveryDays = recoveryDays;
        this.activationDate = activationDate;
        this.description = description;
    }

    public String getHashedPin() {
        return hashedPin;
    }

    public int getPinLength() {
        return pinLength;
    }

    public int getRecoveryDays() {
        return recoveryDays;
    }

    public LocalDateTime getActivationDate() {
        return activationDate;
    }

    public String getDescription() {
        return description;
    }
}
