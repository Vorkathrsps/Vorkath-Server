package com.cryptic.clientscripts.impl.dialogue.information.types.impl;

import com.cryptic.clientscripts.impl.dialogue.information.types.DialogueType;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

/**
 * @Author: Origin
 * @Date: 6/8/24
 */
public record DestroyItemType(Item item, String title, String note) implements DialogueType {
    @Override
    public void send(Player player) {
        if (this.item == null) throw new NullPointerException("parameter item is null.");
        player.getPacketSender().runClientScriptNew(2379);
        player.getPacketSender().runClientScriptNew(814,
            item.getId(),
            item.getAmount() <= 1 ? -1 : item.getAmount(), 0, this.title, this.note);
    }
}
