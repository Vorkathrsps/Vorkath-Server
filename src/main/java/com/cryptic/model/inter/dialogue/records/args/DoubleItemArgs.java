package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.cs2.InterfaceID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Utils;

public record DoubleItemArgs(Item firstItem, Item secondItem, String[] messages, boolean continueButtons) implements Arguements {

    @Override
    public void send(Player player) {
        if (this.firstItem == null) throw new NullPointerException("parameter firstItem is null.");
        if (this.secondItem == null) throw new NullPointerException("parameter secondItem is null.");
        player.getPacketSender().setItemMessage(ComponentID.DIALOG_DOUBLE_SPRITE_SPRITE1, this.firstItem.getId(), this.firstItem.getAmount());
        player.getPacketSender().setComponentText(ComponentID.DIALOG_DOUBLE_SPRITE_TEXT, Utils.joinWithBr(this.messages));
        player.getPacketSender().setItemMessage(ComponentID.DIALOG_DOUBLE_SPRITE_SPRITE2, this.secondItem.getId(), this.secondItem.getAmount());
        player.getPacketSender().setComponentText(InterfaceID.DIALOG_DOUBLE_SPRITE, 4, this.continueButtons ? "Click here to continue" : "");
    }
}
