package com.cryptic.model.content.bank_pin;

import com.cryptic.model.content.bank_pin.dialogue.BankPinSettingsDialogue;
import com.cryptic.model.content.bank_pin.dialogue.ChangeRecoveryDelayDialogue;
import com.cryptic.model.content.bank_pin.dialogue.DeleteBankPinDialogue;
import com.cryptic.model.content.bank_pin.dialogue.IDKBankPinDialogue;
import com.cryptic.model.content.bank_pin.inter.CreateBankPinInterface;
import com.cryptic.model.entity.player.Player;

/**
 * @author lare96 <http://github.com/lare96>
 */
public final class BankPinSettings {

    private final Player player;
    private final BankPin bankPin;

    public BankPinSettings(Player player) {
        this.player = player;
        bankPin = player.getBankPin();
    }

    public void open(int npcId) {
        player.getDialogueManager().start(new BankPinSettingsDialogue(npcId));
    }

    public void createPin(int pinLength) {
        bankPin.setPinInterface(new CreateBankPinInterface(player, pinLength));
        bankPin.getPinInterface().open();
    }

    public void deletePin(int npcId) {
        player.getDialogueManager().start(new DeleteBankPinDialogue(npcId));
    }

    public void dontKnowPin() {
        player.getDialogueManager().start(new IDKBankPinDialogue());
    }

    public void changeRecoveryDelay(int npcId) {
player.getDialogueManager().start(new ChangeRecoveryDelayDialogue(npcId));
    }
}
