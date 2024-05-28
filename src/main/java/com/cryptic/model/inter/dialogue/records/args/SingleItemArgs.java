package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Utils;

public record SingleItemArgs(Item item, String[] messages, boolean continueButton) implements Arguements {

    @Override
    public void send(Player player) {
        if (this.item == null) throw new NullPointerException("parameter item is null.");
        player.getPacketSender().setItemMessage(ComponentID.DIALOG_SPRITE_SPRITE, this.item.getId(), this.item.getAmount());
        player.getPacketSender().setComponentText(ComponentID.DIALOG_SPRITE_TEXT, Utils.joinWithBr(this.messages));
        player.getPacketSender().runClientScriptNew(2868, this.continueButton ? "Click here to continue" : "");
    }
}
