package com.cryptic.clientscripts.impl.dialogue.information.types.impl;

import com.cryptic.clientscripts.ComponentID;
import com.cryptic.model.entity.player.Player;
import com.cryptic.clientscripts.impl.dialogue.util.Expression;
import com.cryptic.clientscripts.impl.dialogue.information.types.DialogueType;
import com.cryptic.utility.Utils;

public record PlayerType(String title, String[] chats, Expression expression, boolean continueButtons) implements DialogueType {
    @Override
    public void send(Player player) {
        player.getPacketSender().setPlayerHeadMesssage(ComponentID.PLAYER_CHAT_HEAD);
        player.getPacketSender().setAnimMessage(ComponentID.PLAYER_CHAT_HEAD, this.expression().getAnimation().getId());
        String finalTitle = this.title.isEmpty() ? player.getUsername() : this.title;
        player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_TITLE, finalTitle);
        player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_MESSAGE, Utils.joinWithBr(chats));
        player.getPacketSender().setComponentVisability(ComponentID.PLAYER_CHAT_CONTINUE, !this.continueButtons);
        player.getPacketSender().setComponentText(ComponentID.PLAYER_CHAT_CONTINUE, "Click here to continue");
    }
}
