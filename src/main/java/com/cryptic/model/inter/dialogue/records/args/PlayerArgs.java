package com.cryptic.model.inter.dialogue.records.args;

import com.cryptic.model.cs2.ComponentID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.model.inter.dialogue.Expression;
import com.cryptic.utility.Utils;

public record PlayerArgs(String title, String[] chats, Expression expression, boolean continueButtons) implements Arguements {
    @Override
    public void send(Player player) {
        player.getPacketSender().setPlayerHeadMesssage(ComponentID.PLAYER_CHAT_HEAD);
        player.getPacketSender().setAnimMessage(ComponentID.PLAYER_CHAT_HEAD, this.expression().getAnimation().getId());
        player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_TITLE, this.title().isBlank() ? this.title() : player.getUsername());
        player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_MESSAGE, Utils.joinWithBr(chats));
        player.getPacketSender().setComponentVisability(ComponentID.PLAYER_CHAT_CONTINUE, !this.continueButtons);
        player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_CONTINUE, "Click here to continue");
    }
}
