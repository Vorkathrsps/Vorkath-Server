package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.entity.player.Player;
import com.cryptic.model.items.Item;

public record DestroyItemArgs(Item item, String title, String note) implements Arguements {
    @Override
    public void send(Player player) {
        if (this.item == null) throw new NullPointerException("parameter item is null.");
        player.getPacketSender().runClientScriptNew(2379);
        player.getPacketSender().runClientScriptNew(814,
            item.getId(),
            item.getAmount(), 0, this.title, this.note);
    }
}
