package com.cryptic.clientscripts.impl.dialogue.information.types.impl;

import com.cryptic.clientscripts.constants.ComponentID;
import com.cryptic.clientscripts.impl.dialogue.information.types.DialogueType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;
import com.cryptic.utility.Utils;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public record SingleItemType(Item item, String[] messages, boolean continueButton) implements DialogueType {

    @Override
    public void send(Player player) {
        if (this.item == null) throw new NullPointerException("parameter item is null.");
        player.getPacketSender().setItemMessage(ComponentID.DIALOG_SPRITE_SPRITE, this.item.getId(), this.item.getAmount());
        player.getPacketSender().setComponentText(ComponentID.DIALOG_SPRITE_TEXT, Utils.joinWithBr(this.messages));
        player.getPacketSender().runClientScriptNew(2868, this.continueButton ? "Click here to continue" : "");
    }
}
